param(
    [ValidateSet('start','stop','status')][string]$Action = 'start',
    [string]$PostgresBin = 'C:\Program Files\PostgreSQL\16\bin'
)
$ErrorActionPreference = 'Stop'
$projectRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
# PostgreSQL Windows CLI can misread accented absolute paths; use its 8.3 alias.
if ($projectRoot -match '[^\x00-\x7F]') {
    Push-Location $projectRoot
    try { $shortRoot = [string](& cmd /c 'for %I in (".") do @echo %~sI') }
    finally { Pop-Location }
    if ($shortRoot -match '[^\x00-\x7F]' -or -not (Test-Path -LiteralPath $shortRoot)) {
        throw 'PostgreSQL Windows can duong dan khong dau. Dung Docker hoac clone du an vao duong dan khong dau.'
    }
    $projectRoot = $shortRoot.Trim()
}
$stateRoot = Join-Path $projectRoot '.local\postgres'
$dataPath = Join-Path $stateRoot 'data'
$configPath = Join-Path $stateRoot 'ket-noi.json'
$pgCtl = Join-Path $PostgresBin 'pg_ctl.exe'
if (-not (Test-Path -LiteralPath $pgCtl)) { throw 'Khong tim thay PostgreSQL 16. Truyen -PostgresBin den thu muc bin da cai.' }
if ($Action -eq 'status') {
    if (-not (Test-Path -LiteralPath $dataPath)) { Write-Host 'Chua khoi tao PostgreSQL rieng cho du an.'; exit 0 }
    & $pgCtl -D $dataPath status
    exit $LASTEXITCODE
}
if ($Action -eq 'stop') {
    if (-not (Test-Path -LiteralPath $dataPath)) { exit 0 }
    & $pgCtl -D $dataPath -m fast -w stop
    exit $LASTEXITCODE
}
New-Item -ItemType Directory -Force -Path $stateRoot | Out-Null
if (-not (Test-Path -LiteralPath $configPath)) {
    if (Test-Path -LiteralPath (Join-Path $dataPath 'PG_VERSION')) { throw 'Data da ton tai nhung thieu ket-noi.json. Khong ghi de.' }
    $randomBytes = New-Object byte[] 32
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($randomBytes)
    $rng.Dispose()
    $settings = @{ username='tutor_app'; password=[Convert]::ToBase64String($randomBytes); port=55432; database='tutor_connect' }
    $settings | ConvertTo-Json | Set-Content -LiteralPath $configPath -Encoding utf8
} else { $settings = Get-Content -LiteralPath $configPath -Raw | ConvertFrom-Json }
if (-not (Test-Path -LiteralPath (Join-Path $dataPath 'PG_VERSION'))) {
    $pwFile = Join-Path $stateRoot 'init-password.txt'
    try {
        [IO.File]::WriteAllText($pwFile, $settings.password, (New-Object Text.UTF8Encoding($false)))
        & (Join-Path $PostgresBin 'initdb.exe') -D $dataPath -U $settings.username '--auth=scram-sha-256' '--encoding=UTF8' '--locale=C' "--pwfile=$pwFile"
        if ($LASTEXITCODE -ne 0) { throw 'initdb that bai. Kiem tra log; khong tu xoa data.' }
    } finally { if (Test-Path -LiteralPath $pwFile) { Remove-Item -LiteralPath $pwFile } }
}
& $pgCtl -D $dataPath status *> $null
if ($LASTEXITCODE -ne 0) {
    $logPath = Join-Path $stateRoot 'server.log'
    $argsLine = '-D "{0}" -l "{1}" -o "-h 127.0.0.1 -p {2}" -w start' -f $dataPath,$logPath,$settings.port
    # WaitForExit waits for pg_ctl only; Start-Process -Wait would also wait for
    # its long-running postgres child on Windows.
    $process = Start-Process -FilePath $pgCtl -ArgumentList $argsLine -WindowStyle Hidden -PassThru
    $process.WaitForExit()
    if ($process.ExitCode -ne 0) { throw "Khong khoi dong duoc PostgreSQL. Xem $logPath" }
}
$previousPassword = $env:PGPASSWORD
try {
    $env:PGPASSWORD = $settings.password
    $psql = Join-Path $PostgresBin 'psql.exe'
    $exists = & $psql -X -h 127.0.0.1 -p $settings.port -U $settings.username -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'tutor_connect'"
    if ($LASTEXITCODE -ne 0) { throw 'Khong ket noi duoc PostgreSQL rieng cua du an.' }
    if ($exists -notcontains '1') {
        & $psql -X -v ON_ERROR_STOP=1 -h 127.0.0.1 -p $settings.port -U $settings.username -d postgres -c 'CREATE DATABASE tutor_connect'
        if ($LASTEXITCODE -ne 0) { throw 'Khong tao duoc database.' }
    }
} finally { $env:PGPASSWORD = $previousPassword }
Write-Host "PostgreSQL cua du an san sang tai 127.0.0.1:$($settings.port). Thong tin ket noi nam trong .local (khong commit)."

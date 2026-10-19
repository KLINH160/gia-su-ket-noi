param([switch]$KiemThu)
$ErrorActionPreference = 'Stop'
$projectRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$settingsPath = Join-Path $projectRoot '.local\postgres\ket-noi.json'
if (-not (Test-Path -LiteralPath $settingsPath)) { throw 'Chay cong-cu/postgres-cuc-bo.ps1 truoc.' }
$settings = Get-Content -LiteralPath $settingsPath -Raw | ConvertFrom-Json
$keys = @('SPRING_PROFILES_ACTIVE','DB_URL','DB_USERNAME','DB_PASSWORD','TEST_DB_URL','TEST_DB_USERNAME','TEST_DB_PASSWORD')
$oldValues = @{}
foreach ($key in $keys) { $oldValues[$key] = [Environment]::GetEnvironmentVariable($key,'Process') }
try {
    $env:SPRING_PROFILES_ACTIVE = 'postgres'
    $env:DB_URL = "jdbc:postgresql://127.0.0.1:$($settings.port)/$($settings.database)"
    $env:DB_USERNAME = $settings.username
    $env:DB_PASSWORD = $settings.password
    $env:TEST_DB_URL = $env:DB_URL
    $env:TEST_DB_USERNAME = $env:DB_USERNAME
    $env:TEST_DB_PASSWORD = $env:DB_PASSWORD
    Push-Location (Join-Path $projectRoot 'backend')
    try {
        if ($KiemThu) { & mvn '-Dmaven.repo.local=../.m2' '-Ppostgres-it' clean verify }
        else {
            # Thu muc socket rieng tranh loi TEMP cua ung dung dong goi MSIX tren Windows.
            $socketDirectory = Join-Path $projectRoot '.local\java-tmp'
            New-Item -ItemType Directory -Path $socketDirectory -Force | Out-Null
            Push-Location $socketDirectory
            try { $shortSocketDirectory = (cmd /c 'for %I in (".") do @echo %~sI').Trim() }
            finally { Pop-Location }
            # Chay JAR tranh loi classpath cua spring-boot:run voi duong dan co dau.
            & mvn '-Dmaven.repo.local=../.m2' '-DskipTests' package
            if ($LASTEXITCODE -ne 0) { throw 'Build backend khong thanh cong.' }
            & java "-Djdk.net.unixdomain.tmpdir=$shortSocketDirectory" -jar target/backend-0.0.1-SNAPSHOT.jar
        }
        if ($LASTEXITCODE -ne 0) { throw 'Maven bao loi. Xem ket qua phia tren.' }
    } finally { Pop-Location }
} finally {
    foreach ($key in $keys) { [Environment]::SetEnvironmentVariable($key,$oldValues[$key],'Process') }
}

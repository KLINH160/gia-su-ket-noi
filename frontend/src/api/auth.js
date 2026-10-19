export class ApiError extends Error {
  constructor(message, status, fieldErrors = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

export async function registerAccount(payload, signal) {
  const response = await fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(payload),
    signal,
  });
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    const fallback = response.status === 503
      ? 'Đăng ký hiện chưa sẵn sàng. Vui lòng thử lại sau.'
      : response.status === 409
        ? 'Email này đã được đăng ký. Vui lòng dùng email khác.'
        : 'Chưa thể đăng ký tài khoản. Vui lòng thử lại.';
    throw new ApiError(data?.message || fallback, response.status, data?.fieldErrors || {});
  }
  if (response.status !== 201 || !data?.id || !['PARENT', 'TUTOR'].includes(data.role)) {
    throw new ApiError('Chưa xác nhận được kết quả đăng ký. Vui lòng kiểm tra lại trước khi gửi lần nữa.', response.status);
  }
  return data;
}

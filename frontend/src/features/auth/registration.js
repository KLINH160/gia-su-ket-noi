export const emptyRegistration = { fullName: '', email: '', password: '', confirmPassword: '', role: 'PARENT' };

export function validateRegistration(values) {
  const errors = {};
  const fullName = values.fullName.trim();
  const email = values.email.trim();
  if (fullName.length < 2 || fullName.length > 120) errors.fullName = 'Họ và tên cần từ 2 đến 120 ký tự.';
  if (email.length > 254 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) errors.email = 'Vui lòng nhập địa chỉ email hợp lệ.';
  if (!values.password.trim() || Array.from(values.password).length < 10) errors.password = 'Mật khẩu cần ít nhất 10 ký tự và không được chỉ gồm khoảng trắng.';
  else if (new TextEncoder().encode(values.password).length > 72) errors.password = 'Mật khẩu quá dài (tối đa 72 byte UTF-8; ký tự có dấu có thể chiếm nhiều byte).';
  if (values.confirmPassword !== values.password || !values.confirmPassword) errors.confirmPassword = 'Mật khẩu nhập lại chưa khớp.';
  if (!['PARENT', 'TUTOR'].includes(values.role)) errors.role = 'Vui lòng chọn phụ huynh hoặc gia sư.';
  return errors;
}

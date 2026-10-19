import { describe, expect, it } from 'vitest';
import { validateRegistration } from './registration.js';

describe('Giới hạn mật khẩu BCrypt', () => {
  const values = { fullName: 'Nguyễn Linh', email: 'linh@example.com', role: 'PARENT' };
  it('chấp nhận mật khẩu đúng 72 byte', () => {
    const password = 'á'.repeat(36);
    expect(validateRegistration({ ...values, password, confirmPassword: password })).toEqual({});
  });
  it('chặn mật khẩu Unicode vượt 72 byte dù chưa đủ 72 ký tự', () => {
    const password = 'á'.repeat(37);
    expect(validateRegistration({ ...values, password, confirmPassword: password }).password).toMatch(/72 byte/);
  });
  it('không cho chọn vai trò quản trị qua kiểm tra phía trình duyệt', () => {
    const password = 'MatKhau123!';
    expect(validateRegistration({ ...values, password, confirmPassword: password, role: 'ADMIN' }).role).toBeTruthy();
  });
  it('đếm ký tự Unicode đúng với backend, không tính một emoji thành hai ký tự', () => {
    const password = '😀'.repeat(5);
    expect(validateRegistration({ ...values, password, confirmPassword: password }).password).toMatch(/10 ký tự/);
  });
  it('không chấp nhận mật khẩu chỉ có khoảng trắng', () => {
    const password = ' '.repeat(12);
    expect(validateRegistration({ ...values, password, confirmPassword: password }).password).toBeTruthy();
  });
});

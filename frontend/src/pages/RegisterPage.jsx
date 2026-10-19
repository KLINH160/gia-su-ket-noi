import { useEffect, useRef, useState } from 'react';
import { ApiError, registerAccount } from '../api/auth.js';
import FormField from '../components/FormField.jsx';
import { emptyRegistration, validateRegistration } from '../features/auth/registration.js';

export default function RegisterPage() {
  const [values, setValues] = useState({ ...emptyRegistration });
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState('');
  const [pending, setPending] = useState(false);
  const [createdAccount, setCreatedAccount] = useState(null);
  const request = useRef(null);
  const inFlight = useRef(false);
  const form = useRef(null);
  const result = useRef(null);
  const focusAfterRequest = useRef(null);

  useEffect(() => () => request.current?.abort(), []);
  useEffect(() => { if (createdAccount) result.current?.focus(); }, [createdAccount]);
  useEffect(() => {
    if (!pending && focusAfterRequest.current) {
      focusFirstError(focusAfterRequest.current);
      focusAfterRequest.current = null;
    }
  }, [pending]);
  const onChange = event => {
    const { name, value } = event.target;
    setValues(previous => ({ ...previous, [name]: value }));
    setErrors(previous => ({ ...previous, [name]: undefined }));
    setMessage('');
  };
  const focusFirstError = fieldErrors => {
    const first = ['role', 'fullName', 'email', 'password', 'confirmPassword'].find(name => fieldErrors[name]);
    if (first) form.current?.querySelector(`[name="${first}"]`)?.focus();
  };
  const submit = async event => {
    event.preventDefault();
    if (inFlight.current) return;
    const validation = validateRegistration(values);
    setErrors(validation);
    setMessage('');
    if (Object.keys(validation).length) {
      focusFirstError(validation);
      return;
    }
    inFlight.current = true;
    setPending(true);
    const controller = new AbortController();
    request.current = controller;
    try {
      const account = await registerAccount({ fullName: values.fullName.trim(), email: values.email.trim(), password: values.password, role: values.role }, controller.signal);
      if (controller.signal.aborted) return;
      setCreatedAccount(account);
      setValues({ ...emptyRegistration });
    } catch (error) {
      if (controller.signal.aborted) return;
      if (error instanceof ApiError) {
        const fields = { ...error.fieldErrors };
        if (error.status === 409) fields.email = fields.email || 'Email này đã được đăng ký. Vui lòng dùng email khác.';
        setErrors(fields);
        setMessage(error.message);
        focusAfterRequest.current = fields;
      } else {
        setMessage('Không nhận được phản hồi từ máy chủ. Vui lòng kiểm tra kết nối trước khi thử lại.');
      }
    } finally {
      inFlight.current = false;
      if (!controller.signal.aborted) setPending(false);
    }
  };

  return <div className="registration-layout">
    <section className="intro" aria-labelledby="intro-heading">
      <p className="eyebrow"><span /> MỖI HÀNH TRÌNH ĐỀU CÓ MỘT KHỞI ĐẦU</p>
      <h1 id="intro-heading">Học cùng người<br /><em>phù hợp với bạn.</em></h1>
      <p className="intro-description">Một nơi để phụ huynh tìm kiếm sự đồng hành, và gia sư chia sẻ kiến thức của mình.</p>
      <div className="journey-card">
        <span className="journey-label">HÀNH TRÌNH KẾT NỐI</span>
        <ol className="journey-steps">
          <li><span className="step-number">01</span><div><strong>Tạo tài khoản</strong><p>Chọn vai trò phù hợp với bạn.</p></div><span className="current-step">Bắt đầu</span></li>
          <li><span className="step-number">02</span><div><strong>Tìm người đồng hành</strong><p>Theo môn học, lịch học và nhu cầu.</p></div></li>
          <li><span className="step-number">03</span><div><strong>Cùng nhau tiến bộ</strong><p>Kết nối và xây dựng hành trình học tập.</p></div></li>
        </ol>
      </div>
      <p className="intro-note">“Mỗi bước tiến nhỏ đều bắt đầu bằng một lựa chọn.”</p>
    </section>
    <section className="registration-panel" aria-labelledby="register-heading">
      {createdAccount ? <div className="registration-success" ref={result} tabIndex={-1}>
        <span className="success-icon" aria-hidden="true">✓</span>
        <p className="eyebrow">ĐÃ TẠO TÀI KHOẢN</p><h2 id="register-heading">Chào {createdAccount.fullName}!</h2>
        <p>Tài khoản <strong>{createdAccount.role === 'PARENT' ? 'phụ huynh' : 'gia sư'}</strong> của bạn đã được tạo với email <strong>{createdAccount.email}</strong>.</p>
        <p className="success-note">Bạn chưa đăng nhập. Chức năng đăng nhập sẽ được bổ sung ở bước phát triển tiếp theo.</p>
        <button type="button" className="primary-button" onClick={() => setCreatedAccount(null)}>Tạo tài khoản khác</button>
      </div> : <>
        <div className="panel-heading"><p className="eyebrow">CHÀO MỪNG BẠN</p><h2 id="register-heading">Tạo tài khoản</h2><p>Bắt đầu với vài thông tin cơ bản.</p></div>
        <form ref={form} onSubmit={submit} noValidate aria-busy={pending}>
          <fieldset className="role-fieldset" disabled={pending} aria-describedby={errors.role ? 'role-error' : undefined}>
            <legend>Bạn tham gia với vai trò nào?</legend>
            <div className="role-options">
              <label className={`role-card ${values.role === 'PARENT' ? 'selected' : ''}`}><input type="radio" name="role" value="PARENT" checked={values.role === 'PARENT'} onChange={onChange} /><span><strong>Phụ huynh</strong><small>Tìm gia sư cho con</small></span></label>
              <label className={`role-card ${values.role === 'TUTOR' ? 'selected' : ''}`}><input type="radio" name="role" value="TUTOR" checked={values.role === 'TUTOR'} onChange={onChange} /><span><strong>Gia sư</strong><small>Đồng hành cùng học sinh</small></span></label>
            </div>
            {errors.role && <span className="field-error" id="role-error">{errors.role}</span>}
          </fieldset>
          <FormField name="fullName" label="Họ và tên" type="text" value={values.fullName} onChange={onChange} error={errors.fullName} autoComplete="name" placeholder="Nguyễn Khánh Linh" maxLength={120} required disabled={pending} />
          <FormField name="email" label="Địa chỉ email" type="email" value={values.email} onChange={onChange} error={errors.email} autoComplete="email" placeholder="ban@example.com" maxLength={254} required disabled={pending} />
          <FormField name="password" label="Mật khẩu" type="password" value={values.password} onChange={onChange} error={errors.password} autoComplete="new-password" hint="Ít nhất 10 ký tự. Nên kết hợp chữ, số và ký tự đặc biệt." required disabled={pending} />
          <FormField name="confirmPassword" label="Nhập lại mật khẩu" type="password" value={values.confirmPassword} onChange={onChange} error={errors.confirmPassword} autoComplete="new-password" required disabled={pending} />
          {message && <div className="form-message" role="alert">{message}</div>}
          <button className="primary-button" type="submit" disabled={pending}>{pending ? 'Đang tạo tài khoản…' : <>Tạo tài khoản <span aria-hidden="true">→</span></>}</button>
          <p className="form-footnote">{values.role === 'TUTOR' ? 'Sau khi đăng ký, hồ sơ giảng dạy cần được duyệt trước khi hiển thị công khai.' : 'Thông tin học sinh sẽ được bổ sung khi bạn gửi yêu cầu học.'}</p>
          <span className="sr-only" role="status">{pending ? 'Đang gửi thông tin đăng ký. Vui lòng chờ.' : ''}</span>
        </form>
      </>}
    </section>
  </div>;
}

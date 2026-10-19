export default function FormField({ name, label, error, hint, ...inputProps }) {
  const description = [hint && `${name}-hint`, error && `${name}-error`].filter(Boolean).join(' ');
  return <div className="form-field">
    <label htmlFor={name}>{label}</label>
    <input id={name} name={name} {...inputProps} aria-invalid={Boolean(error)} aria-describedby={description || undefined} />
    {hint && <span id={`${name}-hint`} className="field-hint">{hint}</span>}
    {error && <span id={`${name}-error`} className="field-error">{error}</span>}
  </div>;
}

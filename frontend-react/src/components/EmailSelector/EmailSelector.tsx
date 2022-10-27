type EmailSelectorProps = {
  emails: string[];
  selectChange: (event: string) => void;
};

export const EmailSelector = ({ emails, selectChange }: EmailSelectorProps) => {
  return (
    <>
      <label htmlFor="email_select">Select an email</label>
      <select
        id="email_select"
        onChange={(e) => {
          selectChange((e.target as HTMLSelectElement).value);
        }}
      >
        {emails.map((e, i) => (
          <option key={i} value={e}>
            {e}
          </option>
        ))}
      </select>
    </>
  );
};

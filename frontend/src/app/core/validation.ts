/** Mirrors the backend rule: at least 8 characters with a letter and a number. */
export const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;

export const PASSWORD_HINT = 'At least 8 characters, including a letter and a number.';

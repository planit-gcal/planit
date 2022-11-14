export type CreateUserRequest = {
  code: string;
  planit_user_id: number | null;
};

export type CreateUserResponse = {
  planit_user_id: number;
  google_account_id: number;
};

export type GetUserEmailsResponse = string[];

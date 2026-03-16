export enum Sex {
  FEMALE = "FEMALE",
  MALE = "MALE",
  INTERSEX = "INTERSEX",
  UNKNOWN = "UNKNOWN",
}

export enum Category {
  NORMAL = "NORMAL",
  INPATIENT = "INPATIENT",
  EMERGENCY = "EMERGENCY",
  OUTPATIENT = "OUTPATIENT",
}

export interface Admission {
  id: string;
  name: string;
  birthday: string; // ISO date string "YYYY-MM-DD"
  sex: Sex;
  category: Category;
  dateOfAdmission: string; // ISO datetime
  externalSystemId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface FieldError {
  field: string;
  message: string;
}

export interface ApiError {
  message: string;
  errors: FieldError[];
  status: number;
}

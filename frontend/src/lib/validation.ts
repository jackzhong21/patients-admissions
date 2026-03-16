import dayjs from "dayjs";

export interface ValidationError {
  field: string;
  message: string;
}

export interface FormValues {
  name: string;
  birthday: string;
  sex: string;
  category: string;
}

export function validateAdmissionForm(
  values: FormValues,
  requireCategory = true
): ValidationError[] {
  const errors: ValidationError[] = [];

  if (!values.name || values.name.trim() === "") {
    errors.push({ field: "name", message: "Name is required" });
  }

  if (!values.birthday) {
    errors.push({ field: "birthday", message: "Birthday is required" });
  } else if (dayjs(values.birthday).isAfter(dayjs(), "day")) {
    errors.push({
      field: "birthday",
      message: "Birthday cannot be in the future",
    });
  }

  if (!values.sex) {
    errors.push({ field: "sex", message: "Sex is required" });
  }

  if (requireCategory && !values.category) {
    errors.push({ field: "category", message: "Category is required" });
  }

  return errors;
}

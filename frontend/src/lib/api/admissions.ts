import { Admission, ApiError, PagedResponse } from "@/types/admission";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const error: ApiError = await res.json();
    throw error;
  }
  if (res.status === 204) {
    return undefined as unknown as T;
  }
  return res.json();
}

export async function fetchAdmissions(
  page = 0,
  size = 20
): Promise<PagedResponse<Admission>> {
  const res = await fetch(
    `${BASE_URL}/api/admissions?page=${page}&size=${size}`
  );
  return handleResponse<PagedResponse<Admission>>(res);
}

export async function createAdmission(data: {
  name: string;
  birthday: string;
  sex: string;
  category: string;
}): Promise<Admission> {
  const res = await fetch(`${BASE_URL}/api/admissions`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return handleResponse<Admission>(res);
}

export async function updateAdmission(
  id: string,
  data: { name: string; birthday: string; sex: string; category: string }
): Promise<Admission> {
  const res = await fetch(`${BASE_URL}/api/admissions/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return handleResponse<Admission>(res);
}

export async function deleteAdmission(id: string): Promise<void> {
  const res = await fetch(`${BASE_URL}/api/admissions/${id}`, {
    method: "DELETE",
  });
  return handleResponse<void>(res);
}

export async function createExternalAdmission(data: {
  name: string;
  birthday: string;
  sex: string;
  category: string;
  externalSystemId: string;
}): Promise<Admission> {
  const res = await fetch(`${BASE_URL}/api/admissions/external`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return handleResponse<Admission>(res);
}

export async function updateExternalAdmission(
  id: string,
  data: { name: string; birthday: string; sex: string }
): Promise<Admission> {
  const res = await fetch(`${BASE_URL}/api/admissions/external/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return handleResponse<Admission>(res);
}

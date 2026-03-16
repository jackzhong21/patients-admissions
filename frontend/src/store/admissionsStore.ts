import { create } from "zustand";
import { Admission, ApiError, PagedResponse } from "@/types/admission";
import { fetchAdmissions, deleteAdmission } from "@/lib/api/admissions";

interface AdmissionsState {
  admissions: Admission[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  loading: boolean;
  error: string | null;
  fetchAdmissions: (page?: number, size?: number) => Promise<void>;
  deleteAdmission: (id: string) => Promise<void>;
  setPage: (page: number) => void;
  setSize: (size: number) => void;
}

const useAdmissionsStore = create<AdmissionsState>((set, get) => ({
  admissions: [],
  totalElements: 0,
  totalPages: 0,
  page: 0,
  size: 20,
  loading: false,
  error: null,

  fetchAdmissions: async (page?: number, size?: number) => {
    const currentPage = page ?? get().page;
    const currentSize = size ?? get().size;
    set({ loading: true, error: null });
    try {
      const data: PagedResponse<Admission> = await fetchAdmissions(
        currentPage,
        currentSize
      );

      set({
        admissions: data.content,
        totalElements: data.totalElements,
        totalPages: data.totalPages,
        page: data.page,
        size: data.size,
        loading: false,
      });
    } catch (err) {
      const apiError = err as ApiError;
      set({
        loading: false,
        error: apiError?.message ?? "Failed to fetch admissions",
      });
    }
  },

  deleteAdmission: async (id: string) => {
    set({ loading: true, error: null });
    try {
      await deleteAdmission(id);
      await get().fetchAdmissions();
    } catch (err) {
      const apiError = err as ApiError;
      set({
        loading: false,
        error: apiError?.message ?? "Failed to delete admission",
      });
    }
  },

  setPage: (page: number) => {
    set({ page });
    get().fetchAdmissions(page, get().size);
  },

  setSize: (size: number) => {
    set({ size, page: 0 });
    get().fetchAdmissions(0, size);
  },
}));

export default useAdmissionsStore;

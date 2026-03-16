import { act } from "@testing-library/react";
import * as api from "@/lib/api/admissions";
import { Admission, Category, Sex } from "@/types/admission";

jest.mock("@/lib/api/admissions");

const mockAdmission: Admission = {
  id: "test-id-1",
  name: "Jane Doe",
  birthday: "1990-05-15",
  sex: Sex.FEMALE,
  category: Category.INPATIENT,
  dateOfAdmission: "2026-03-16T09:00:00Z",
  externalSystemId: null,
  createdAt: "2026-03-16T09:00:00Z",
  updatedAt: "2026-03-16T09:00:00Z",
};

describe("admissionsStore", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Reset the store between tests
    jest.resetModules();
  });

  it("fetch populates store with admissions", async () => {
    const mockFetch = jest.spyOn(api, "fetchAdmissions").mockResolvedValue({
      content: [mockAdmission],
      totalElements: 1,
      totalPages: 1,
      page: 0,
      size: 20,
    });

    const { default: useAdmissionsStore } = await import(
      "@/store/admissionsStore"
    );

    await act(async () => {
      await useAdmissionsStore.getState().fetchAdmissions();
    });

    const state = useAdmissionsStore.getState();
    expect(state.admissions).toHaveLength(1);
    expect(state.admissions[0].name).toBe("Jane Doe");
    expect(state.totalElements).toBe(1);
    expect(mockFetch).toHaveBeenCalled();
  });

  it("sets error state on API failure", async () => {
    jest.spyOn(api, "fetchAdmissions").mockRejectedValue({
      message: "Network error",
      status: 500,
      errors: [],
    });

    const { default: useAdmissionsStore } = await import(
      "@/store/admissionsStore"
    );

    await act(async () => {
      await useAdmissionsStore.getState().fetchAdmissions();
    });

    const state = useAdmissionsStore.getState();
    expect(state.error).toBe("Network error");
    expect(state.loading).toBe(false);
  });
});

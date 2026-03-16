import { act } from "@testing-library/react";
import { fetchAdmissions } from "@/lib/api/admissions";
import useAdmissionsStore from "@/store/admissionsStore";
import { Admission, Category, Sex } from "@/types/admission";

jest.mock("@/lib/api/admissions");

const mockedFetchAdmissions = jest.mocked(fetchAdmissions);

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
    useAdmissionsStore.setState({
      admissions: [],
      totalElements: 0,
      totalPages: 0,
      page: 0,
      size: 20,
      loading: false,
      error: null,
    });
  });

  it("fetch populates store with admissions", async () => {
    mockedFetchAdmissions.mockResolvedValue({
      content: [mockAdmission],
      totalElements: 1,
      totalPages: 1,
      page: 0,
      size: 20,
    });

    await act(async () => {
      await useAdmissionsStore.getState().fetchAdmissions();
    });

    const state = useAdmissionsStore.getState();
    expect(state.admissions).toHaveLength(1);
    expect(state.admissions[0].name).toBe("Jane Doe");
    expect(state.totalElements).toBe(1);
    expect(mockedFetchAdmissions).toHaveBeenCalled();
  });

  it("sets error state on API failure", async () => {
    mockedFetchAdmissions.mockRejectedValue({
      message: "Network error",
      status: 500,
      errors: [],
    });

    await act(async () => {
      await useAdmissionsStore.getState().fetchAdmissions();
    });

    const state = useAdmissionsStore.getState();
    expect(state.error).toBe("Network error");
    expect(state.loading).toBe(false);
  });
});

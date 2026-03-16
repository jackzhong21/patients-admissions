import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import AdmissionTable from "@/components/admissions/AdmissionTable";
import useAdmissionsStore from "@/store/admissionsStore";
import { Admission, Category, Sex } from "@/types/admission";

// Mock the store
jest.mock("@/store/admissionsStore");
jest.mock("next/navigation", () => ({
  useRouter: () => ({ push: jest.fn() }),
}));

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

const defaultStoreState = {
  admissions: [mockAdmission],
  totalElements: 1,
  page: 0,
  size: 20,
  loading: false,
  error: null,
  fetchAdmissions: jest.fn(),
  deleteAdmission: jest.fn(),
  setPage: jest.fn(),
  setSize: jest.fn(),
};

beforeEach(() => {
  (useAdmissionsStore as unknown as jest.Mock).mockReturnValue(defaultStoreState);
});

describe("AdmissionTable", () => {
  it("renders column headers", () => {
    render(
      <AdmissionTable
        admissions={[mockAdmission]}
        totalElements={1}
        page={0}
        size={20}
      />
    );
    expect(screen.getByText("Name")).toBeInTheDocument();
    expect(screen.getByText("Birthday")).toBeInTheDocument();
    expect(screen.getByText("Sex")).toBeInTheDocument();
    expect(screen.getByText("Category")).toBeInTheDocument();
  });

  it("renders admission row data", () => {
    render(
      <AdmissionTable
        admissions={[mockAdmission]}
        totalElements={1}
        page={0}
        size={20}
      />
    );
    expect(screen.getByText("Jane Doe")).toBeInTheDocument();
    expect(screen.getByText("1990-05-15")).toBeInTheDocument();
  });

  it("opens delete dialog when delete button clicked", () => {
    render(
      <AdmissionTable
        admissions={[mockAdmission]}
        totalElements={1}
        page={0}
        size={20}
      />
    );
    const deleteButton = screen.getByLabelText("delete Jane Doe");
    fireEvent.click(deleteButton);
    expect(screen.getByText("Confirm Delete")).toBeInTheDocument();
  });

  it("shows empty state when no admissions", () => {
    render(
      <AdmissionTable admissions={[]} totalElements={0} page={0} size={20} />
    );
    expect(screen.getByText("No admissions found")).toBeInTheDocument();
  });

  it("renders pagination", () => {
    render(
      <AdmissionTable
        admissions={[mockAdmission]}
        totalElements={100}
        page={0}
        size={20}
      />
    );
    expect(screen.getByRole("combobox")).toBeInTheDocument(); // rows per page select
  });
});

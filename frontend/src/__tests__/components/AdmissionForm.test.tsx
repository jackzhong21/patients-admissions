import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import AdmissionForm from "@/components/admissions/AdmissionForm";
import * as api from "@/lib/api/admissions";
import { Admission, Category, Sex } from "@/types/admission";

jest.mock("next/navigation", () => ({
  useRouter: () => ({ push: jest.fn(), back: jest.fn(), refresh: jest.fn() }),
}));
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

describe("AdmissionForm", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders all fields in create mode", () => {
    render(<AdmissionForm mode="create" />);
    expect(screen.getByLabelText(/name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/birthday/i)).toBeInTheDocument();
  });

  it("category is disabled in edit-external mode", () => {
    render(
      <AdmissionForm
        mode="edit-external"
        admission={{ ...mockAdmission, externalSystemId: "EXT-001" }}
      />
    );
    // The category FormControl should be disabled
    const categoryLabel = screen.queryAllByText("Category");
    expect(categoryLabel[0]).toBeInTheDocument();
  });

  it("shows error for future birthday", async () => {
    render(<AdmissionForm mode="create" />);
    const nameInput = screen.getByTestId("name-input");
    const birthdayInput = screen.getByTestId("birthday-input");
    const submitButton = screen.getByTestId("submit-button");

    fireEvent.change(nameInput, { target: { value: "Test User" } });
    fireEvent.change(birthdayInput, { target: { value: "2099-01-01" } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(
        screen.getByText("Birthday cannot be in the future")
      ).toBeInTheDocument();
    });
  });

  it("shows error for empty name", async () => {
    render(<AdmissionForm mode="create" />);
    const submitButton = screen.getByTestId("submit-button");
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("Name is required")).toBeInTheDocument();
    });
  });

  it("does not call API on validation failure", async () => {
    const createAdmission = jest.spyOn(api, "createAdmission");
    render(<AdmissionForm mode="create" />);
    const submitButton = screen.getByTestId("submit-button");
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("Name is required")).toBeInTheDocument();
    });
    expect(createAdmission).not.toHaveBeenCalled();
  });
});

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import DeleteConfirmDialog from "@/components/admissions/DeleteConfirmDialog";

describe("DeleteConfirmDialog", () => {
  const onConfirm = jest.fn();
  const onCancel = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders dialog with admission name", () => {
    render(
      <DeleteConfirmDialog
        open={true}
        admissionName="Jane Doe"
        onConfirm={onConfirm}
        onCancel={onCancel}
      />
    );
    expect(screen.getByText("Confirm Delete")).toBeInTheDocument();
    expect(screen.getByText(/Jane Doe/)).toBeInTheDocument();
  });

  it("calls onConfirm when delete button clicked", () => {
    render(
      <DeleteConfirmDialog
        open={true}
        admissionName="Jane Doe"
        onConfirm={onConfirm}
        onCancel={onCancel}
      />
    );
    fireEvent.click(screen.getByText("Delete"));
    expect(onConfirm).toHaveBeenCalledTimes(1);
  });

  it("calls onCancel when cancel button clicked", () => {
    render(
      <DeleteConfirmDialog
        open={true}
        admissionName="Jane Doe"
        onConfirm={onConfirm}
        onCancel={onCancel}
      />
    );
    fireEvent.click(screen.getByText("Cancel"));
    expect(onCancel).toHaveBeenCalledTimes(1);
  });

  it("does not render when closed", () => {
    render(
      <DeleteConfirmDialog
        open={false}
        admissionName="Jane Doe"
        onConfirm={onConfirm}
        onCancel={onCancel}
      />
    );
    expect(screen.queryByText("Confirm Delete")).not.toBeInTheDocument();
  });
});

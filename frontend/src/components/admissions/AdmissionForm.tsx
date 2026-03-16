"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import FormHelperText from "@mui/material/FormHelperText";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import Paper from "@mui/material/Paper";
import Select from "@mui/material/Select";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import Stack from "@mui/material/Stack";
import { Admission, Category, Sex } from "@/types/admission";
import { ApiError } from "@/types/admission";
import {
  createAdmission,
  updateAdmission,
  updateExternalAdmission,
} from "@/lib/api/admissions";
import { validateAdmissionForm, ValidationError } from "@/lib/validation";
import ErrorAlert from "@/components/common/ErrorAlert";

export type FormMode = "create" | "edit-regular" | "edit-external";

interface AdmissionFormProps {
  mode: FormMode;
  admission?: Admission;
}

export default function AdmissionForm({ mode, admission }: AdmissionFormProps) {
  const router = useRouter();
  const isExternal = mode === "edit-external";

  const [name, setName] = useState(admission?.name ?? "");
  const [birthday, setBirthday] = useState(admission?.birthday ?? "");
  const [sex, setSex] = useState<string>(admission?.sex ?? "");
  const [category, setCategory] = useState<string>(admission?.category ?? "");
  const [fieldErrors, setFieldErrors] = useState<ValidationError[]>([]);
  const [apiError, setApiError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const getFieldError = (field: string) =>
    fieldErrors.find((e) => e.field === field)?.message;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const errors = validateAdmissionForm(
      { name, birthday, sex, category },
      !isExternal
    );
    if (errors.length > 0) {
      setFieldErrors(errors);
      return;
    }
    setFieldErrors([]);
    setApiError(null);
    setSubmitting(true);

    try {
      if (mode === "create") {
        await createAdmission({ name, birthday, sex, category });
      } else if (mode === "edit-regular") {
        await updateAdmission(admission!.id, { name, birthday, sex, category });
      } else {
        await updateExternalAdmission(admission!.id, { name, birthday, sex });
      }
      router.push("/admissions");
      router.refresh();
    } catch (err) {
      const apiErr = err as ApiError;
      setApiError(apiErr?.message ?? "An error occurred");
    } finally {
      setSubmitting(false);
    }
  };

  const title =
    mode === "create"
      ? "Create Admission"
      : isExternal
      ? "Edit External Admission"
      : "Edit Admission";

  return (
    <Paper sx={{ p: 4, maxWidth: 600, mx: "auto", mt: 4 }}>
      <Typography variant="h5" gutterBottom>
        {title}
      </Typography>

      {apiError && <ErrorAlert message={apiError} />}

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <Stack spacing={3}>
          <TextField
            label="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            error={!!getFieldError("name")}
            helperText={getFieldError("name")}
            required
            fullWidth
            inputProps={{ "data-testid": "name-input" }}
          />

          <TextField
            label="Birthday"
            type="date"
            value={birthday}
            onChange={(e) => setBirthday(e.target.value)}
            error={!!getFieldError("birthday")}
            helperText={getFieldError("birthday")}
            required
            fullWidth
            InputLabelProps={{ shrink: true }}
            inputProps={{ "data-testid": "birthday-input" }}
          />

          <FormControl fullWidth error={!!getFieldError("sex")} required>
            <InputLabel>Sex</InputLabel>
            <Select
              value={sex}
              label="Sex"
              onChange={(e) => setSex(e.target.value)}
              inputProps={{ "data-testid": "sex-select" }}
            >
              {Object.values(Sex).map((s) => (
                <MenuItem key={s} value={s}>
                  {s}
                </MenuItem>
              ))}
            </Select>
            {getFieldError("sex") && (
              <FormHelperText>{getFieldError("sex")}</FormHelperText>
            )}
          </FormControl>

          <FormControl fullWidth disabled={isExternal}>
            <InputLabel>Category</InputLabel>
            <Select
              value={category}
              label="Category"
              onChange={(e) => setCategory(e.target.value)}
              inputProps={{ "data-testid": "category-select" }}
            >
              {Object.values(Category).map((c) => (
                <MenuItem key={c} value={c}>
                  {c}
                </MenuItem>
              ))}
            </Select>
            {getFieldError("category") && (
              <FormHelperText error>{getFieldError("category")}</FormHelperText>
            )}
          </FormControl>

          {isExternal && admission?.externalSystemId && (
            <TextField
              label="External System ID"
              value={admission.externalSystemId}
              disabled
              fullWidth
              inputProps={{ "data-testid": "external-id-input" }}
            />
          )}

          <Stack direction="row" spacing={2} justifyContent="flex-end">
            <Button
              variant="outlined"
              onClick={() => router.back()}
              disabled={submitting}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              disabled={submitting}
              data-testid="submit-button"
            >
              {mode === "create" ? "Create" : "Save"}
            </Button>
          </Stack>
        </Stack>
      </Box>
    </Paper>
  );
}

"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import AddIcon from "@mui/icons-material/Add";
import useAdmissionsStore from "@/store/admissionsStore";
import AdmissionTable from "@/components/admissions/AdmissionTable";
import LoadingSpinner from "@/components/common/LoadingSpinner";
import ErrorAlert from "@/components/common/ErrorAlert";

export default function AdmissionsPage() {
  const router = useRouter();
  const {
    admissions,
    totalElements,
    page,
    size,
    loading,
    error,
    fetchAdmissions,
  } = useAdmissionsStore();

  useEffect(() => {
    fetchAdmissions();
  }, [fetchAdmissions]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
      >
        <Typography variant="h4" component="h1">
          Patient Admissions
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => router.push("/admissions/create")}
          data-testid="create-admission-button"
        >
          New Admission
        </Button>
      </Box>

      {error && <ErrorAlert message={error} />}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <AdmissionTable
          admissions={admissions}
          totalElements={totalElements}
          page={page}
          size={size}
        />
      )}
    </Container>
  );
}

"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Container from "@mui/material/Container";
import { Admission } from "@/types/admission";
import { fetchAdmissions } from "@/lib/api/admissions";
import AdmissionForm, { FormMode } from "@/components/admissions/AdmissionForm";
import LoadingSpinner from "@/components/common/LoadingSpinner";
import ErrorAlert from "@/components/common/ErrorAlert";

export default function EditAdmissionPage() {
  const params = useParams();
  const id = params.id as string;
  const [admission, setAdmission] = useState<Admission | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function load() {
      try {
        // Fetch all and find by id (simple approach for this implementation)
        const data = await fetchAdmissions(0, 1000);
        const found = data.content.find((a) => a.id === id);
        if (!found) {
          setError("Admission not found");
        } else {
          setAdmission(found);
        }
      } catch {
        setError("Failed to load admission");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [id]);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorAlert message={error} />;
  if (!admission) return null;

  const mode: FormMode =
    admission.externalSystemId != null ? "edit-external" : "edit-regular";

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <AdmissionForm mode={mode} admission={admission} />
    </Container>
  );
}

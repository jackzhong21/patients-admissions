import Container from "@mui/material/Container";
import AdmissionForm from "@/components/admissions/AdmissionForm";

export default function CreateAdmissionPage() {
  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <AdmissionForm mode="create" />
    </Container>
  );
}

"use client";

import Alert from "@mui/material/Alert";
import AlertTitle from "@mui/material/AlertTitle";

interface ErrorAlertProps {
  message: string;
  title?: string;
}

export default function ErrorAlert({ message, title = "Error" }: ErrorAlertProps) {
  return (
    <Alert severity="error" sx={{ mb: 2 }}>
      <AlertTitle>{title}</AlertTitle>
      {message}
    </Alert>
  );
}

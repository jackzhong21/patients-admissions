"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import Tooltip from "@mui/material/Tooltip";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Chip from "@mui/material/Chip";
import { Admission } from "@/types/admission";
import DeleteConfirmDialog from "./DeleteConfirmDialog";
import useAdmissionsStore from "@/store/admissionsStore";

interface AdmissionTableProps {
  admissions: Admission[];
  totalElements: number;
  page: number;
  size: number;
}

export default function AdmissionTable({
  admissions,
  totalElements,
  page,
  size,
}: AdmissionTableProps) {
  const router = useRouter();
  const { setPage, setSize, deleteAdmission } = useAdmissionsStore();
  const [deleteDialog, setDeleteDialog] = useState<{
    open: boolean;
    id: string;
    name: string;
  }>({ open: false, id: "", name: "" });

  const handleEdit = (admission: Admission) => {
    router.push(`/admissions/${admission.id}/edit`);
  };

  const handleDeleteClick = (admission: Admission) => {
    setDeleteDialog({ open: true, id: admission.id, name: admission.name });
  };

  const handleDeleteConfirm = async () => {
    await deleteAdmission(deleteDialog.id);
    setDeleteDialog({ open: false, id: "", name: "" });
  };

  const handleDeleteCancel = () => {
    setDeleteDialog({ open: false, id: "", name: "" });
  };

  return (
    <Box>
      <TableContainer component={Paper}>
        <Table aria-label="admissions table">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Birthday</TableCell>
              <TableCell>Sex</TableCell>
              <TableCell>Category</TableCell>
              <TableCell>Date of Admission</TableCell>
              <TableCell>External ID</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {admissions.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  No admissions found
                </TableCell>
              </TableRow>
            ) : (
              admissions.map((admission) => (
                <TableRow key={admission.id} hover>
                  <TableCell>{admission.name}</TableCell>
                  <TableCell>{admission.birthday}</TableCell>
                  <TableCell>{admission.sex}</TableCell>
                  <TableCell>
                    <Chip label={admission.category} size="small" />
                  </TableCell>
                  <TableCell>
                    {new Date(admission.dateOfAdmission).toLocaleString('en-AU')}
                  </TableCell>
                  <TableCell>
                    {admission.externalSystemId ? (
                      <Chip
                        label={admission.externalSystemId}
                        size="small"
                        color="secondary"
                      />
                    ) : (
                      "—"
                    )}
                  </TableCell>
                  <TableCell align="right">
                    <Tooltip title="Edit">
                      <IconButton
                        size="small"
                        onClick={() => handleEdit(admission)}
                        aria-label={`edit ${admission.name}`}
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Delete">
                      <IconButton
                        size="small"
                        onClick={() => handleDeleteClick(admission)}
                        aria-label={`delete ${admission.name}`}
                        color="error"
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={(_, newPage) => setPage(newPage)}
        rowsPerPage={size}
        onRowsPerPageChange={(e) => setSize(parseInt(e.target.value, 10))}
        rowsPerPageOptions={[10, 20, 50]}
      />
      <DeleteConfirmDialog
        open={deleteDialog.open}
        admissionName={deleteDialog.name}
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
      />
    </Box>
  );
}

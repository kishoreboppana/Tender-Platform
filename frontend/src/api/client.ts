import axios from 'axios';

export const api = axios.create({
  baseURL: '/api',
  headers: { Accept: 'application/json' },
});

export interface DashboardSummary {
  activeTenders: number;
  closingToday: number;
  closingIn7Days: number;
  pendingApprovals: number;
  missingMandatoryDocs: number;
  overdueActions: number;
  redRisks: number;
  winRatePercent: number;
  digestStatus: string;
  digestLastRun: string;
  tenantName: string;
}

export interface TenderRow {
  tenderId: string;
  customer: string;
  name: string;
  owner: string;
  stage: string;
  priority: string;
  closingAt: string;
  health: string;
  completionPercent: number;
}

export interface TenderDetail extends TenderRow {
  customerCode: string;
  tenderReference: string;
  tenderType: string;
  estimatedValue: number | null;
  currency: string;
  submissionMode: string;
  approvalStatus: string;
  latestStatusComment: string | null;
}

export interface CustomerOption {
  id: number;
  customerCode: string;
  name: string;
  customerType: string;
  contactEmail?: string;
  active: boolean;
}

export interface CreateTenderPayload {
  name: string;
  customerId: number;
  tenderReference: string;
  tenderType: string;
  estimatedValue?: number;
  currency?: string;
  closingAt: string;
  tenderOwnerName: string;
  priority: string;
  latestStatusComment?: string;
  completionPercent?: number;
}

export async function fetchDashboardSummary(): Promise<DashboardSummary> {
  const { data } = await api.get<DashboardSummary>('/dashboard/summary');
  return data;
}

export async function fetchTenders(): Promise<TenderRow[]> {
  const { data } = await api.get<{ items: TenderRow[]; total: number }>('/tenders');
  return data.items;
}

export interface TenderDocumentRow {
  id: number;
  docName: string;
  fileName: string;
  fileSizeBytes: number;
  approvalStatus: string;
  uploadedBy: string;
  uploadedAt: string;
  submittedAt: string | null;
  rejectionReason?: string | null;
  reviewedAt?: string | null;
  reviewedBy?: string | null;
}

export interface DocumentActionResult {
  item?: TenderDocumentRow;
  tenderHealth: string;
  tenderStatusComment: string | null;
}

export type UploadTenderDocumentResult = DocumentActionResult & { item: TenderDocumentRow };

export async function fetchTenderDocuments(tenderId: string): Promise<TenderDocumentRow[]> {
  const { data } = await api.get<{ items: TenderDocumentRow[]; total: number }>(
    `/tenders/${encodeURIComponent(tenderId)}/documents`,
  );
  return data.items;
}

export async function uploadTenderDocument(
  tenderId: string,
  docName: string,
  file: File,
): Promise<UploadTenderDocumentResult> {
  const form = new FormData();
  form.append('docName', docName);
  form.append('file', file);
  const { data } = await api.post<UploadTenderDocumentResult>(
    `/tenders/${encodeURIComponent(tenderId)}/documents`,
    form,
    { headers: { 'Content-Type': 'multipart/form-data' } },
  );
  return data;
}

export function getTenderDocumentDownloadUrl(tenderId: string, documentId: number): string {
  return `/api/tenders/${encodeURIComponent(tenderId)}/documents/${documentId}/download`;
}

export async function approveTenderDocument(
  tenderId: string,
  documentId: number,
): Promise<DocumentActionResult> {
  const { data } = await api.post<DocumentActionResult>(
    `/tenders/${encodeURIComponent(tenderId)}/documents/${documentId}/approve`,
  );
  return data;
}

export async function rejectTenderDocument(
  tenderId: string,
  documentId: number,
  reason: string,
): Promise<DocumentActionResult> {
  const { data } = await api.post<DocumentActionResult>(
    `/tenders/${encodeURIComponent(tenderId)}/documents/${documentId}/reject`,
    { reason },
  );
  return data;
}

export async function deleteTenderDocument(
  tenderId: string,
  documentId: number,
): Promise<DocumentActionResult> {
  const { data } = await api.delete<DocumentActionResult>(
    `/tenders/${encodeURIComponent(tenderId)}/documents/${documentId}`,
  );
  return data;
}

export async function fetchTenderById(tenderId: string): Promise<TenderDetail> {
  try {
    const { data } = await api.get<{ item: TenderDetail }>(`/tenders/${encodeURIComponent(tenderId)}`);
    return data.item;
  } catch (err) {
    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as { response?: { status?: number } };
      if (axiosErr.response?.status === 404) {
        throw new Error(`Tender not found: ${tenderId}`);
      }
    }
    throw err;
  }
}

export interface CreateCustomerPayload {
  customerCode: string;
  name: string;
  customerType: string;
  contactEmail?: string;
  active?: boolean;
}

export async function fetchCustomers(): Promise<CustomerOption[]> {
  const { data } = await api.get<{ items: CustomerOption[]; total: number }>('/customers');
  return data.items;
}

export async function createCustomer(payload: CreateCustomerPayload): Promise<CustomerOption> {
  const { data } = await api.post<{ item: CustomerOption }>('/customers', payload);
  return data.item;
}

export async function createTender(payload: CreateTenderPayload): Promise<TenderRow> {
  const { data } = await api.post<{ item: TenderRow }>('/tenders', payload);
  return data.item;
}

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

export interface CustomerOption {
  id: number;
  customerCode: string;
  name: string;
  customerType: string;
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

export async function fetchCustomers(): Promise<CustomerOption[]> {
  const { data } = await api.get<{ items: CustomerOption[]; total: number }>('/customers');
  return data.items;
}

export async function createTender(payload: CreateTenderPayload): Promise<TenderRow> {
  const { data } = await api.post<{ item: TenderRow }>('/tenders', payload);
  return data.item;
}

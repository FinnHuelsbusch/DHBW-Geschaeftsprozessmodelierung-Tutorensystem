export interface Page {
    elementsPerPage: number,
    totalElements: number,
    totalPages: number,
    currentPage: number,
    sorting?: Array<{ attribute: string, order: "asc" | "desc" }>
}
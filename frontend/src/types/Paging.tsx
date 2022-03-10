export interface Page {
    elementsPerPage: number,
    totalElements: number,
    totalPages: number,
    currentPage: number,
    sorting?: Sorting
}

export type Sorting = {
    attribute?: string,
    order?: "asc" | "desc"
};
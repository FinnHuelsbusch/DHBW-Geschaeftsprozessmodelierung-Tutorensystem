import { List, Empty } from 'antd';
import { PaginationConfig } from 'antd/lib/pagination';
import React, { PropsWithChildren } from 'react';
import { Page } from '../../types/Paging';

export const PageDefaults = {
    possibleSizes: [5, 10, 20]
};

type PagingListProps<DataType> = {
    listData: Array<DataType> | undefined,
    listItem: (itemData: DataType) => JSX.Element,
    page: Page,
    onPageChanged: (selectedPageOneBased: number, pageSize: number) => void,
    possiblePageSizes?: number[],
    showSizeChanger?: boolean,
    isLoading: boolean,
    loadingItem: () => JSX.Element,
    [x: string]: any, // varargs
}

declare type DataType = any;

const PagingList: React.FC<PagingListProps<DataType>> = (
    {
        listData, listItem, page, onPageChanged,
        possiblePageSizes = PageDefaults.possibleSizes,
        showSizeChanger = true,
        isLoading,
        loadingItem,
        ...varargs
    }: PropsWithChildren<PagingListProps<DataType>>
) => {
    if (!isLoading && (!listData || listData.length === 0)) {
        return (
            <div className="no-data-info">
                <Empty
                    description="Keine Daten verfügbar">
                </Empty>
            </div>
        );
    } else {
        const pagination: PaginationConfig = {
            pageSize: page.elementsPerPage,
            total: page.totalElements,
            // ant uses one-based index, but consumer expects zero-based
            // convert one-based to zero-based
            onChange: (page: number, pageSize: number) => onPageChanged(page - 1, pageSize),
            // convert zero-based to one-based
            current: page.currentPage + 1,
            // show page size options only if the corresponding callback is defined
            // note: onChange method is also triggered on page change 
            showSizeChanger: showSizeChanger,
            pageSizeOptions: possiblePageSizes,
        };

        return (
            <List
                {...varargs}
                dataSource={isLoading
                    // loading: fill set page size with loading items
                    ? Array.apply(null, Array(pagination.pageSize)).map((x, i) => i)
                    : listData}
                pagination={pagination}
                renderItem={(item: DataType) => {
                    if (isLoading) {
                        return loadingItem();
                    } else {
                        return listItem(item);
                    }
                }}
            />
        );
    }
}


export default PagingList;
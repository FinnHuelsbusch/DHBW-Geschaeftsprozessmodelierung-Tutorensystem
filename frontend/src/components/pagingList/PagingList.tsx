import { List, Empty } from 'antd';
import React, { PropsWithChildren } from 'react';
import { Page } from '../../types/Paging';

export const PageDefaults = {
    pageSize: 5,
    possibleSizes: [5, 10, 20]
};

type PagingListProps<DataType> = {
    listData: Array<DataType> | undefined,
    listItem: (itemData: DataType) => JSX.Element,
    page: Page | undefined,
    onPageChanged: (selectedPageOneBased: number, pageSize: number) => void,
    possiblePageSizes?: number[],
    defaultPageSize?: number,
    onPageSizeChanged?: (currentPage: number, newSize: number) => void
    [x: string]: any, // varargs
}

declare type DataType = any;

const PagingList: React.FC<PagingListProps<DataType>> = (
    {
        listData, listItem, page, onPageChanged,
        possiblePageSizes = PageDefaults.possibleSizes,
        defaultPageSize = PageDefaults.pageSize,
        onPageSizeChanged,
        ...varargs
    }: PropsWithChildren<PagingListProps<DataType>>
) => {

    if (listData && page && listData.length > 0) {
        const pageSizeChangeDefined = typeof onPageSizeChanged === 'function';
        return (
            <List
                {...varargs}
                dataSource={listData}
                pagination={{
                    pageSize: page.elementsPerPage,
                    total: page.totalElements,
                    // ant uses one-based index, but consumer expects zero-based
                    // convert one-based to zero-based
                    onChange: (page: number, pageSize: number) => onPageChanged(page - 1, pageSize),
                    // convert zero-based to one-based
                    current: page.currentPage + 1,
                    showSizeChanger: pageSizeChangeDefined,
                    pageSizeOptions: pageSizeChangeDefined ? possiblePageSizes : undefined,
                    defaultPageSize: pageSizeChangeDefined ? defaultPageSize : undefined,
                    // onShowSizeChange: pageSizeChangeDefined ? onPageSizeChanged : undefined
                }}
                renderItem={(item: DataType) => listItem(item)}
            />
        );
    } else {
        return (
            <div className="no-data-info">
                <Empty
                    description="Keine Daten verfügbar">
                </Empty>
            </div>
        );
    }
}


export default PagingList;
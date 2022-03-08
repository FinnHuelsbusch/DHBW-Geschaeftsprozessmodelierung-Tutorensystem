import { List, Empty } from 'antd';
import React, { PropsWithChildren } from 'react';
import { Page } from '../../types/Paging';

type PagingListProps<DataType> = {
    listData: Array<DataType> | undefined,
    listItem: (itemData: DataType) => JSX.Element,
    page: Page | undefined,
    onPageChanged: (selectedPageOneBased: number) => void,
    [x: string]: any, // varargs
}

declare type DataType = any;

const PagingList: React.FC<DataType> = (
    { listData, listItem, page, onPageChanged, ...varargs }: PropsWithChildren<PagingListProps<DataType>>
) => {

    if (listData && page && listData.length > 0) {
        return (
            <List
                {...varargs}
                dataSource={listData}
                pagination={{
                    pageSize: page.elementsPerPage,
                    total: page.totalElements,
                    // ant uses one-based index, but consumer expects zero-based
                    // convert one-based to zero-based
                    onChange: page => onPageChanged(page - 1),
                    // convert zero-based to one-based
                    current: page.currentPage + 1
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
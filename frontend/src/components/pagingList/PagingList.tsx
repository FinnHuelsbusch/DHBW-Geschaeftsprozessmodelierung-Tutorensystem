import { List, Empty } from 'antd';
import React, { PropsWithChildren } from 'react';
import { Page } from '../../types/Paging';
import './PagingList.scss';


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
                    pageSize: page.size,
                    total: page.totalElements,
                    onChange: page => onPageChanged(page),
                    // adjust from zero-based to one-based index (for ant)
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
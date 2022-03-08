import { Card, List } from 'antd';
import Title from 'antd/lib/typography/Title';
import moment from 'moment';
import React from 'react';
import { Page } from '../../types/Paging';
import { Tutorial } from '../../types/Tutorial';
import PagingList from '../pagingList/PagingList';

const TutorialsOverview: React.FC = () => {

    const demoListData: Array<Tutorial> = [
        { title: "Hallo 1", description: "Desc", start: new Date(), end: new Date() },
        { title: "Hallo 2", description: "Desc", start: new Date(), end: new Date() },
        // { title: "Hallo 3", description: "Desc", start: new Date(), end: new Date() },
        // { title: "Hallo 4", description: "Desc", start: new Date(), end: new Date() },
        // { title: "Hallo 5", description: "Desc", start: new Date(), end: new Date() },
    ];

    const listItem = (tutorial: Tutorial) => {

        return (
            <List.Item>
                <Card>
                    {tutorial.title}
                </Card>
            </List.Item>
        );
    };

    const page : Page = {
        currentPage: 1,
        size: 3,
        totalElements: 5,
        totalPages: 2,
    };

    const fetchNewPage = (newPage: number) => {
        page.currentPage = newPage;
    }

    return (
        <>
            <Title level={1}>
                Tutorien
            </Title>
            <div>Filter</div>
            <PagingList
                listData={demoListData}
                listItem={listItem}
                page={page}
                onPageChanged={fetchNewPage}
            />
        </>
    );
};

export default TutorialsOverview;
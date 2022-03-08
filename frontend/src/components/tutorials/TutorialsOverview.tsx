import { Button, Card, List, Tag } from 'antd';
import Meta from 'antd/lib/card/Meta';
import Paragraph from 'antd/lib/typography/Paragraph';
import Text from 'antd/lib/typography/Text';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useEffect, useState } from 'react';
import { getFilteredTutorials } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { Page } from '../../types/Paging';
import { Tutorial, TutorialFilter } from '../../types/Tutorial';
import PagingList from '../pagingList/PagingList';

const TutorialsOverview: React.FC = () => {

    const authContext = useContext(AuthContext);

    const [filter, setFilter] = useState<TutorialFilter>({
        text: undefined,
        startDateFrom: undefined,
        startDateTo: undefined,
        specialisationCourseIds: undefined,
        currentPage: 0,
        elementsPerPage: 2,
        totalElements: 0,
        totalPages: 0,
        tutorials: undefined
    });

    useEffect(() => {
        fetchNewPage(0);
    }, []);

    const fetchNewPage = (newPage: number) => {
        console.log("fetching page",newPage);
        return getFilteredTutorials({ ...filter, currentPage: newPage })
            .then(res => {
                console.log("received:", res)
                setFilter({
                    ...filter,
                    tutorials: res.tutorials as Array<Tutorial>,
                    currentPage: res.currentPage,
                    totalPages: res.totalPages,
                    totalElements: res.totalElements
                });
            });
    };

    const FilterBar = () => {
        
        return(
            <div>Hallo</div>
        );
    };

    const mockSpecialisationCourses = [
        { id: 1, name: "WI SE" },
        { id: 2, name: "WI SC" },
        { id: 3, name: "BWL DBM" },
        { id: 4, name: "AI" },
    ];

    const listItem = (tutorial: Tutorial) => {
        return (
            <List.Item>
                <Card
                    onClick={e => alert("Click")}
                    hoverable
                    title={tutorial.title}
                    extra={authContext.loggedUser &&
                        <Button type='link'>Vormerken</Button>
                    }
                >
                    <Paragraph ellipsis={{ rows: 2, expandable: false }}>
                        {tutorial.description}
                    </Paragraph>
                    <div>
                        {mockSpecialisationCourses.map(course => (
                            <Tag>{course.name}</Tag>
                        ))}
                    </div>
                </Card>
            </List.Item>
        );
    };

    return (
        <>
            <Title level={1}>
                Tutorien
            </Title>
            <div>Filter</div>
            <PagingList
                grid={{
                    gutter: 16,
                    xs: 1,
                    sm: 1,
                    md: 2,
                    lg: 2,
                    xl: 3,
                    xxl: 5,
                }}
                listData={filter.tutorials}
                listItem={listItem}
                page={{ ...filter }}
                onPageChanged={fetchNewPage}
            />
        </>
    );
};

export default TutorialsOverview;
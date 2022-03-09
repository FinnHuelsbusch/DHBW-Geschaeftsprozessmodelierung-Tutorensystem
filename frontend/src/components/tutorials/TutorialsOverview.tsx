import { Button, Card, Col, DatePicker, Divider, Form, Input, List, Row, Select, Slider, Tag } from 'antd';
import Meta from 'antd/lib/card/Meta';
import { useForm } from 'antd/lib/form/Form';
import Paragraph from 'antd/lib/typography/Paragraph';
import Text from 'antd/lib/typography/Text';
import Title from 'antd/lib/typography/Title';
import { format } from 'path';
import React, { useContext, useEffect, useState } from 'react';
import { getFilteredTutorials } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { Page } from '../../types/Paging';
import { Tutorial, TutorialFilter, TutorialFilterResponse } from '../../types/Tutorial';
import PagingList, { PageDefaults } from '../pagingList/PagingList';

const TutorialsOverview: React.FC = () => {

    const authContext = useContext(AuthContext);

    const [filter, setFilter] = useState<TutorialFilter>({
        text: undefined,
        startDateFrom: undefined,
        startDateTo: undefined,
        specialisationCourseIds: undefined,
        sorting: undefined,
        page: 0,
        elementsPerPage: PageDefaults.pageSize
    });

    const [filteredTutorials, setFilteredTutorials] = useState<TutorialFilterResponse>({
        tutorials: undefined,
        currentPage: 0,
        totalElements: 0,
        totalPages: 0,
    });

    useEffect(() => {
        fetchPage(0);
    }, []);

    useEffect(() => {
        // re-fetch the first page upon filter change
        fetchPage(0);
        console.log("filter after change:", filter);
    }, [filter]);

    const fetchPage = (page: number) => {
        console.log("fetching page", page);
        getFilteredTutorials(filter)
            .then(filteredTutorials => {
                console.log("received:", filteredTutorials);
                setFilteredTutorials(filteredTutorials);
            });
    };

    const onFilterChange = () => {
        const formFilter = { ...form.getFieldsValue() };
        console.log(form.getFieldsValue());
        setFilter({
            ...filter,
            text: formFilter.text,
            startDateFrom: formFilter.timerange ? formFilter.timerange[0] : undefined,
            startDateTo: formFilter.timerange ? formFilter.timerange[1] : undefined,
        });
    };

    const onPageChanged = (page: number, pageSize: number) => {
        console.log("on page changed");
        setFilter({
            ...filter,
            // if page size has changed, re-fetch the first page
            page: filter.elementsPerPage !== pageSize ? 0 : page,
            elementsPerPage: pageSize
        });
    }

    const onPageSizeChanged = (currentPage: number, newSize: number) => {
        // re-fetch the first page after number of page elements was changed
        console.log("new size:", newSize);
        setFilter({ ...filter, elementsPerPage: newSize, page: 0 });
    }

    const [form] = useForm();


    const FilterBar = () => {

        const resetFilter = () => {
            form.resetFields();
            onFilterChange();
        };

        const onSearchClick = () => {
            onFilterChange();
        };

        return (
            <Form
                form={form}
                className="product-filter-form"
                // onChange={e => onFilterChange()}
                onFinish={onFilterChange}
            >
                <Row gutter={24}>
                    <Col flex="1 1 300px">
                        <Form.Item
                            name="text"
                            label="Suchen">
                            <Input
                                allowClear
                                placeholder="Titel, Beschreibung..." />
                        </Form.Item>
                    </Col>

                    <Col flex="1 1 300px">
                        <Row style={{ display: 'block' }}>
                            <Form.Item
                                label="Zeitraum"
                                name="timerange">
                                <DatePicker.RangePicker
                                    placeholder={["Anfang", "Ende"]}
                                    format="DD.MM.YYYY"
                                />
                            </Form.Item>
                        </Row>
                    </Col>
                </Row>

                <Row>
                    <Col span={24} style={{ textAlign: 'right' }}>
                        <Button type="primary" htmlType='submit' onClick={e => onSearchClick()}>
                            Suchen
                        </Button>
                        <Button
                            style={{ margin: '0 8px' }}
                            onClick={() => resetFilter()}>
                            Zurücksetzen
                        </Button>
                    </Col>
                </Row>
            </Form>
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
            <Divider>Suchkriterien</Divider>
            <FilterBar />
            <Divider />
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
                listData={filteredTutorials.tutorials}
                listItem={listItem}
                page={{
                    elementsPerPage: filter.elementsPerPage,
                    currentPage: filteredTutorials.currentPage,
                    totalElements: filteredTutorials.totalElements,
                    totalPages: filteredTutorials.totalPages
                } as Page}
                onPageChanged={onPageChanged}
                onPageSizeChanged={onPageSizeChanged}
            />
        </>
    );
};

export default TutorialsOverview;
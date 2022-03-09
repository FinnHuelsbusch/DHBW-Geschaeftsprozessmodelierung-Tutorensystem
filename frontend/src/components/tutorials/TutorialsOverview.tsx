import { Button, Card, Col, DatePicker, Divider, Form, Input, List, message, Row, Select, Skeleton, Slider, Tag } from 'antd';
import Meta from 'antd/lib/card/Meta';
import { useForm } from 'antd/lib/form/Form';
import Paragraph from 'antd/lib/typography/Paragraph';
import Text from 'antd/lib/typography/Text';
import Title from 'antd/lib/typography/Title';
import moment from 'moment';
import { format } from 'path';
import React, { useContext, useEffect, useState } from 'react';
import { getFilteredTutorials, getRequestError } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { Page } from '../../types/Paging';
import { getErrorMessageString } from '../../types/RequestError';
import { Tutorial, TutorialFilter, TutorialFilterResponse } from '../../types/Tutorial';
import { formatDate } from '../../utils/DateTimeHandling';
import PagingList, { PageDefaults } from '../pagingList/PagingList';
import TutorialDetails from './TutorialDetails';

const TutorialsOverview: React.FC = () => {

    const authContext = useContext(AuthContext);
    const [loading, setLoading] = useState(true);
    const [form] = useForm();

    const [selectedTutorial, setSelectedTutorial] = useState<Tutorial | undefined>(undefined);

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
        // initially fetch first page
        fetchPage(0);
    }, []);

    useEffect(() => {
        // re-fetch the first page upon filter change
        fetchPage(0);
    }, [filter]);

    const fetchPage = (page: number) => {
        setLoading(true);
        getFilteredTutorials(filter)
            .then(filteredTutorials => {
                console.log("received:", filteredTutorials);
                setFilteredTutorials(filteredTutorials);
                setLoading(false);
            }).catch(err => {
                console.log("err", err);
                const reqErr = getRequestError(err);
                message.error(getErrorMessageString(reqErr.errorCode));
                setLoading(false);
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
        setFilter({
            ...filter,
            // if page size has changed, re-fetch the first page
            page: filter.elementsPerPage !== pageSize ? 0 : page,
            elementsPerPage: pageSize
        });
    }


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
                        <Button
                            type="primary"
                            loading={loading}
                            htmlType='submit'
                            onClick={e => onSearchClick()}>
                            Suchen
                        </Button>
                        <Button
                            style={{ margin: '0 8px' }}
                            disabled={loading}
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
                    onClick={e => setSelectedTutorial(tutorial)}
                    hoverable
                    title={tutorial.title}
                    extra={authContext.loggedUser &&
                        <Button type='link'>Vormerken</Button>
                    }
                >
                    <Paragraph ellipsis={{ rows: 2, expandable: false }}>
                        {tutorial.description}
                    </Paragraph>
                    <Paragraph>
                        {formatDate(tutorial.start)} - {formatDate(tutorial.end)}, Umfang {tutorial.durationMinutes} Minuten
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

    const loadingItem = () => {
        return (
            <List.Item>
                <Card>
                    <Skeleton active paragraph={{ rows: 2 }} />
                </Card>
            </List.Item>
        );
    }

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
                page={{
                    elementsPerPage: filter.elementsPerPage,
                    currentPage: filteredTutorials.currentPage,
                    totalElements: filteredTutorials.totalElements,
                    totalPages: filteredTutorials.totalPages
                } as Page}
                onPageChanged={onPageChanged}
                listData={filteredTutorials.tutorials}
                listItem={listItem}
                isLoading={loading}
                loadingItem={loadingItem}
            />

            <TutorialDetails
                tutorial={selectedTutorial}
                onClose={() => setSelectedTutorial(undefined)}
            />
        </>
    );
};

export default TutorialsOverview;
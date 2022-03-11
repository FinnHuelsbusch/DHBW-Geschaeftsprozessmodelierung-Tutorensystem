import { Button, Card, Col, DatePicker, Divider, Form, Input, List, message, Row, Select, Skeleton, Tag, Tooltip } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import Paragraph from 'antd/lib/typography/Paragraph';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useEffect, useState } from 'react';
import { getFilteredTutorials, getRequestError } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { Page } from '../../types/Paging';
import { getErrorMessageString } from '../../types/RequestError';
import { Tutorial, TutorialFilter, TutorialFilterResponse } from '../../types/Tutorial';
import { formatDate } from '../../utils/DateTimeHandling';
import PagingList from '../pagingList/PagingList';
import TutorialDetails from './TutorialDetails';
import { UserOutlined } from '@ant-design/icons'

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
        sorting: { attribute: "none", order: undefined },
        page: 0,
        elementsPerPage: 2,
    });

    const [filteredTutorials, setFilteredTutorials] = useState<TutorialFilterResponse>({
        tutorials: undefined,
        currentPage: 0,
        totalElements: 0,
        totalPages: 0,
    });

    useEffect(() => {
        // re-fetch upon filter change (also called on initial loading of the page)
        fetchPage();
        console.log("filter", filter);
    }, [filter]);

    const fetchPage = () => {
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

        const onSortingChange = (value: any, option: any) => {
            // called whenever any of the two sorting inputs are changed
            const sortingAttribute = form.getFieldValue("sortingAttribute");
            if (sortingAttribute !== "none") {
                const sortingType = form.getFieldValue("sortingType");
                setFilter({
                    ...filter,
                    sorting: { attribute: sortingAttribute, order: sortingType }
                });
            } else {
                // reset sorting
                setFilter({
                    ...filter,
                    sorting: { attribute: sortingAttribute, order: undefined }
                });
            }
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
                        <Form.Item
                            label="Startdatum"
                            name="timerange">
                            <DatePicker.RangePicker
                                style={{ width: '100%' }}
                                allowClear
                                placeholder={["Anfang", "Ende"]}
                                format="DD.MM.YYYY"
                            />
                        </Form.Item>
                    </Col>

                    <Col flex="1 1 300px">
                        <Form.Item
                            label="Studienrichtung:"
                            name="specialisationCourses">
                            <Select>
                                <Select.Option>Hier TreeSelect mit spec. courses</Select.Option>
                            </Select>
                        </Form.Item>
                    </Col>

                    <Col flex="1 1 300px">
                        <Form.Item label="Sortierung">
                            <Input.Group compact>
                                <Form.Item
                                    noStyle
                                    name="sortingAttribute">
                                    <Select
                                        defaultValue={"none"}
                                        style={{ minWidth: '100pt' }}
                                        onChange={onSortingChange}>
                                        <Select.Option key="none">Keine</Select.Option>
                                        <Select.Option key="title">Titel</Select.Option>
                                        <Select.Option key="start">Startdatum</Select.Option>
                                    </Select>
                                </Form.Item>
                                <Form.Item
                                    noStyle
                                    name="sortingType">
                                    <Select
                                        disabled={filter.sorting.attribute === "none" ? true : false}
                                        defaultValue={"asc"}
                                        style={{ minWidth: '100pt' }}
                                        onChange={onSortingChange}>
                                        <Select.Option key="asc">Aufsteigend</Select.Option>
                                        <Select.Option key="desc">Absteigend</Select.Option>
                                    </Select>
                                </Form.Item>
                            </Input.Group>
                        </Form.Item>
                    </Col>
                </Row>

                <Row>
                    <Col span={24} style={{ textAlign: 'right' }}>
                        <Button
                            type="primary"
                            loading={loading}
                            htmlType='submit'
                            onClick={e => onSearchClick()}
                        >
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
                    extra={
                        <>
                            {authContext.loggedUser &&
                                <Button type='link'>Vormerken</Button>}
                            <Tooltip title={`${tutorial.numberOfParticipants} Teilnehmer`}>
                                <UserOutlined /> {tutorial.numberOfParticipants}
                            </Tooltip>
                        </>
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
                <Card
                    extra={<></>}
                >
                    <Skeleton active paragraph={{ rows: 2 }}>

                    </Skeleton>
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
                defaultPageSize={2}
                possiblePageSizes={[1, 2, 3]}
            />

            <TutorialDetails
                tutorial={selectedTutorial}
                onClose={() => setSelectedTutorial(undefined)}
            />
        </>
    );
};

export default TutorialsOverview;
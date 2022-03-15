import { Button, Card, Checkbox, Col, DatePicker, Divider, Form, Input, List, message, Row, Select, Skeleton, Space, Tag, Tooltip, TreeSelect } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import Paragraph from 'antd/lib/typography/Paragraph';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useEffect, useState } from 'react';
import { getCoursesWithTitleAndSpecialisations, getFilteredTutorials, getRequestError } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { Page } from '../../types/Paging';
import { getErrorMessageString } from '../../types/RequestError';
import { Tutorial, TutorialFilter, TutorialFilterResponse } from '../../types/Tutorial';
import { formatDate } from '../../utils/DateTimeHandling';
import PagingList from '../pagingList/PagingList';
import { UserOutlined, ClockCircleOutlined, StarOutlined, StarFilled, CheckCircleTwoTone, SoundOutlined } from '@ant-design/icons'
import { useNavigate, useNavigationType } from 'react-router-dom';
import { CheckboxChangeEvent } from 'antd/lib/checkbox';
import { TreeNode } from 'antd/lib/tree-select';
import { CourseWithTitleAndSpecialisations } from '../../types/Course';

const TutorialsOverview: React.FC = () => {

    const authContext = useContext(AuthContext);
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [form] = useForm();

    const [courses, setCourses] = useState<CourseWithTitleAndSpecialisations[]>([]);

    const [filter, setFilter] = useState<TutorialFilter>({
        text: undefined,
        startDateFrom: undefined,
        startDateTo: undefined,
        specialisationCourseIds: undefined,
        selectMarked: false,
        selectParticipates: false,
        selectHolds: false,
        sorting: { attribute: "none", order: undefined },
        page: 0,
        elementsPerPage: 5,
    });

    const [filteredTutorials, setFilteredTutorials] = useState<TutorialFilterResponse>({
        tutorials: undefined,
        currentPage: 0,
        totalElements: 0,
        totalPages: 0,
    });

    const navigationType = useNavigationType();

    useEffect(() => {
        getCoursesWithTitleAndSpecialisations().then(courses => {
            setCourses(courses);
        }, err => {
            message.error(getErrorMessageString(getRequestError(err).errorCode))
        });
    }, []);

    useEffect(() => {
        const savedFilter = sessionStorage.getItem("tutorialFilter");
        // apply saved filter if it exists and if user navigated back
        if (savedFilter && navigationType === "POP") {
            sessionStorage.removeItem("tutorialFilter");
            const filterObj = JSON.parse(savedFilter);
            form.setFieldsValue({ ...filterObj });
            setFilter(filterObj);
            return;
        }

        // re-fetch upon filter change (also called on initial loading of the page)
        fetchPage();
    }, [filter]);

    const fetchPage = () => {
        setLoading(true);
        getFilteredTutorials(filter)
            .then(filteredTutorials => {
                setFilteredTutorials(filteredTutorials);
                setLoading(false);
            }).catch(err => {
                const reqErr = getRequestError(err);
                message.error(getErrorMessageString(reqErr.errorCode));
                setLoading(false);
            });
    };

    const onFilterChange = () => {
        const formFilter = { ...form.getFieldsValue() };
        const dateFormat = "YYYY-MM-DD";
        const startDateFromString = formFilter.timerange ? formatDate(formFilter.timerange[0], dateFormat) : undefined;
        const startDateToString = formFilter.timerange ? formatDate(formFilter.timerange[1], dateFormat) : undefined;
        setFilter({
            ...filter,
            // always go back to first page
            page: 0,
            // manually map all attributes
            text: formFilter.text,
            startDateFrom: startDateFromString,
            startDateTo: startDateToString,
            specialisationCourseIds: formFilter.specialisationCourseIds,
            selectMarked: formFilter.selectMarked,
            selectParticipates: formFilter.selectParticipates
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
                    sorting: { attribute: sortingAttribute, order: sortingType },
                    // always go back to first page
                    page: 0,
                });
            } else {
                // reset sorting
                setFilter({
                    ...filter,
                    sorting: { attribute: sortingAttribute, order: undefined },
                    // always go back to first page
                    page: 0,
                });
            }
        };

        const onSelectMarkedChange = (e: CheckboxChangeEvent) => {
            setFilter({
                ...filter,
                selectMarked: e.target.checked,
                // always go back to first page
                page: 0,
            });
        };

        const onSelectParticipatesChange = (e: CheckboxChangeEvent) => {
            setFilter({
                ...filter,
                selectParticipates: e.target.checked,
                // always go back to first page
                page: 0,
            });
        };

        const onSelectHoldsChange = (e: CheckboxChangeEvent) => {
            setFilter({
                ...filter,
                selectHolds: e.target.checked,
                // always go back to first page
                page: 0,
            });
        };

        return (
            <Form
                form={form}
                onFinish={onFilterChange}
            >
                <Row gutter={24}>
                    <Col flex="0.5 1 300px">
                        <Form.Item
                            name="text"
                            label="Suchen">
                            <Input
                                allowClear
                                placeholder="Titel, Beschreibung..." />
                        </Form.Item>
                    </Col>

                    <Col flex="0.5 1 300px">
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
                            label="Studienrichtungen:"
                            name="specialisationCourseIds">
                            <TreeSelect
                                placeholder="Auswählen..."
                                showArrow
                                allowClear
                                maxTagCount={3}
                                filterTreeNode
                                treeNodeFilterProp="title"
                                treeCheckable={true}
                                showCheckedStrategy={TreeSelect.SHOW_CHILD}
                            >
                                {courses.map(course => (
                                    <TreeNode title={course.title} value={course.title}>
                                        {course.specialisationCourses.map(specialisationCourse => (
                                            <TreeNode key={specialisationCourse.id} title={specialisationCourse.title} value={specialisationCourse.id} />
                                        ))}
                                    </TreeNode>
                                ))}
                            </TreeSelect>
                        </Form.Item>
                    </Col>
                </Row>

                <Row>
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

                {authContext.loggedUser && <Row>
                    <Col flex="1 1 100px">
                        <Form.Item
                            name="selectMarked"
                            valuePropName="checked">
                            <Checkbox onChange={onSelectMarkedChange}>
                                <StarFilled style={{ color: '#ffd805' }} /> Markiert
                            </Checkbox>
                        </Form.Item>
                    </Col>

                    <Col flex="1 1 100px">
                        <Form.Item
                            name="selectParticipates"
                            valuePropName="checked">
                            <Checkbox onChange={onSelectParticipatesChange}>
                                <UserOutlined /> Teilgenommen
                            </Checkbox>
                        </Form.Item>
                    </Col>

                    <Col flex="1 1 100px">
                        <Form.Item
                            name="selectHolds"
                            valuePropName="checked">
                            <Checkbox onChange={onSelectHoldsChange}>
                                <SoundOutlined /> Halten
                            </Checkbox>
                        </Form.Item>
                    </Col>
                </Row>}

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

    const onTutorialClick = (tutorialId: number) => {
        sessionStorage.setItem("tutorialFilter", JSON.stringify(filter));
        navigate(`/tutorials/${tutorialId}`);
    };

    const listItem = (tutorial: Tutorial) => {
        const cardExtra = (
            <Space>
                {tutorial.participates
                    &&
                    <Tooltip
                        title="Sie nehmen an diesem Tutorium teil"
                    >
                        <CheckCircleTwoTone twoToneColor='#52c41a' style={{ fontSize: '18pt' }} />
                    </Tooltip>
                }
                {tutorial.isMarked ?
                    <StarFilled style={{ color: '#ffd805', fontSize: '18pt' }} />
                    : <StarOutlined style={{ fontSize: '18pt' }} />}
            </Space>
        );

        return (
            <List.Item>
                <Card
                    onClick={e => onTutorialClick(tutorial.id)}
                    hoverable
                    title={tutorial.title}
                    extra={authContext.loggedUser && cardExtra}
                >
                    <Paragraph ellipsis={{ rows: 2, expandable: false }}>
                        {tutorial.description}
                    </Paragraph>
                    <Paragraph>
                        <ClockCircleOutlined /> {formatDate(tutorial.start)} - {formatDate(tutorial.end)}, Gesamtumfang {tutorial.durationMinutes} Minuten
                    </Paragraph>
                    <Paragraph>
                        <UserOutlined /> {tutorial.numberOfParticipants} Teilnehmer
                    </Paragraph>
                    {tutorial.specialisationCourses.map(specialisationCourse => (
                        <Tag>{specialisationCourse.course.abbreviation} {specialisationCourse.abbreviation}</Tag>
                    ))}
                </Card>
            </List.Item >
        );
    };

    const loadingItem = () => {
        return (
            <List.Item>
                <Card
                    // extra is here to achieve similar looks to normal card
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
            <Divider>{filteredTutorials.totalElements} Ergebnisse</Divider>
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
                possiblePageSizes={[5, 10, 20]}
            />
        </>
    );
};

export default TutorialsOverview;
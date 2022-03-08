import { Button, Card, Col, DatePicker, Form, Input, List, Row, Select, Slider, Tag } from 'antd';
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
        console.log("fetching page", newPage);
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

    const onFilterChange = () => {
        const formFilter = { ...form.getFieldsValue() };
        console.log(form.getFieldsValue());
        setFilter({
            ...filter,
            text: formFilter.text,
            startDateFrom: formFilter.timerange ?? formFilter.timerange[0],
            startDateTo: formFilter.timerange ?? formFilter.timerange[1],
        });
    };

    const [form] = useForm();


    const FilterBar = () => {

        const pageSizeValues = [5, 10, 20];

        const resetFilter = () => {
            form.resetFields();
            onFilterChange();
        };

        return (
            <Form
                form={form}
                className="product-filter-form"
                onChange={e => onFilterChange()}
                onFinish={onFilterChange}
            >
                <Row gutter={24}>
                    <Col flex="1 1 300px">
                        <Form.Item
                            name="text"
                            label="Suchen">
                            <Input
                                placeholder="Titel..." />
                        </Form.Item>
                    </Col>

                    <Col flex="1 1 300px">
                        <Row style={{ display: 'block' }}>
                            <Form.Item
                                label="Zeitraum"
                                name="timerange"
                            >
                                <DatePicker.RangePicker
                                    placeholder={["Anfang", "Ende"]}
                                    format="DD.MM.YYYY"
                                />
                            </Form.Item>
                        </Row>
                    </Col>

                    <Col flex="1 1 300px">
                        <Form.Item
                            label="Elemente pro Seite"
                            name="pageSize"
                            initialValue={pageSizeValues[0]}>
                            <Select
                                style={{ width: 80 }}
                                onChange={e => onFilterChange()}>
                                {pageSizeValues.map(number =>
                                    <Select.Option
                                        key={`${number}`}
                                        value={`${number}`}>
                                        {number}
                                    </Select.Option>
                                )}
                            </Select>
                        </Form.Item>
                    </Col>
                </Row>

                <Row>
                    <Col span={24} style={{ textAlign: 'right' }}>
                        <Button type="primary" htmlType="submit">
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
            <FilterBar />
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
import { Button, DatePicker, Divider, Form, Input, message, Modal, Select, TreeSelect } from "antd"
import { useForm } from "antd/lib/form/Form";
import { useEffect, useState } from "react";
import { getCoursesWithTitleAndSpecialisations, getRequestError } from "../../api/api";
import { CourseWithTitleAndSpecialisations } from "../../types/Course";
import TextArea from "antd/lib/input/TextArea";
import moment from "moment";
import { User } from "../../types/User";
import { getErrorMessageString } from "../../types/RequestError";
import { TreeNode } from "antd/lib/tree-select";
import EmailFormInput from '../inputs/EmailFormInput';

interface Props {
    isModalVisible: boolean,
    setIsTutorialOfferModalVisible: (visible: boolean) => void
}

const TutorialCreateModal: React.FC<Props> = ({ isModalVisible, setIsTutorialOfferModalVisible }) => {

    const [form] = useForm();
    const [courses, setCourses] = useState<CourseWithTitleAndSpecialisations[]>([]);

    const onFinish = (values: any) => {
        console.log("values", values);

        setIsTutorialOfferModalVisible(false);
        form.resetFields();
    };

    const onCancel = () => {
        setIsTutorialOfferModalVisible(false)
    }

    useEffect(() => {
        // initial opening of page: get available courses
        if (isModalVisible) {
            getCoursesWithTitleAndSpecialisations().then(Courses => {
                setCourses(Courses);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });
        }
    }, [isModalVisible]);



    return (

        <Modal
            destroyOnClose={true}
            visible={isModalVisible}
            onCancel={onCancel}
            title={"Tutoriumsangebot erstellen"}
            width={600}
            footer={[
                <Button
                    type="primary"
                    htmlType="submit"
                    onClick={e => form.submit()}>
                    Kontaktieren
                </Button>
            ]}
        >

            <div>

                <Divider>Daten für angebotene Tutorien</Divider>
                <Form
                    form={form}
                    labelCol={{ span: 6 }}
                    wrapperCol={{ span: 18 }}
                    onFinish={onFinish}
                >

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Titel:"
                        name="title"
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Beschreibung:"
                        name="description"
                    >
                        <TextArea rows={4} placeholder="Maximal 500 Zeichen" maxLength={500} showCount />
                    </Form.Item>

                    <Form.Item
                        label="Zeitraum:"
                        name="timerange"
                        rules={[{ required: true }]}
                    >
                        <DatePicker.RangePicker
                            placeholder={["Anfang", "Ende"]}
                            format="DD.MM.YYYY"
                        />
                    </Form.Item>
                    <Form.Item
                        rules={[{ required: true }]}
                        label="Studiengänge:"
                        name="Courses"

                    >
                        <TreeSelect
                            // treeCheckable={true}
                            showCheckedStrategy={TreeSelect.SHOW_CHILD}
                        >
                            {courses.map(course => (
                                <TreeNode title={course.title} value={course.title}>
                                    {course.specialisationCourses.map(specialisationCourse => (
                                        <TreeNode che title={specialisationCourse.title} value={specialisationCourse.title} />
                                    ))}
                                </TreeNode>
                            ))}
                        </TreeSelect>
                    </Form.Item>
                    <Form.Item>
                        <Select mode="tags" tokenSeparators={[" ",","]}>
                            
                        </Select>
                    </Form.Item>
                </Form>
                                        




            </div>

        </Modal>
    )
}

export default TutorialCreateModal;
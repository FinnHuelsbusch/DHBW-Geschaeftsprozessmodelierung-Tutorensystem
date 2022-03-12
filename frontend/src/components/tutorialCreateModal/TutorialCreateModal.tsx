import { Button, DatePicker, Divider, Form, Input, InputNumber, message, Modal, Select, TimePicker, TreeSelect } from "antd"
import { useForm } from "antd/lib/form/Form";
import { useDebugValue, useEffect, useState } from "react";
import { getCoursesWithTitleAndSpecialisations, getRequestError, getUsersWithNameAndMailAndId } from "../../api/api";
import { CourseWithTitleAndSpecialisations } from "../../types/Course";
import TextArea from "antd/lib/input/TextArea";
import { UserWithMailAndNameAndId } from "../../types/User";
import { getErrorMessageString } from "../../types/RequestError";
import { TreeNode } from "antd/lib/tree-select";
import { Tutor, Tutorial } from "../../types/Tutorial";
import moment from "moment";


interface Props {
    isModalVisible: boolean,
    setIsTutorialOfferModalVisible: (visible: boolean) => void
}

const TutorialCreateModal: React.FC<Props> = ({ isModalVisible, setIsTutorialOfferModalVisible }) => {

    const [users, setUsers] = useState<UserWithMailAndNameAndId[]>([]);
    const [form] = useForm();
    const [courses, setCourses] = useState<CourseWithTitleAndSpecialisations[]>([]);

    const onFinish = (values: any) => {
        console.log("values", values);
        const durationMinutes = parseInt(values.durationMinutes.split(":")[0]) * 60 + parseInt(values.durationMinutes.split(":")[1]);
        const tutorium = {
            title: values.title,
            description: values.description,
            start: moment(values.timerange[0]).format("DD.MM.YYYY"),
            end: moment(values.timerange[1]).format("DD.MM.YYYY"),
            durationMinutes: durationMinutes,
            tutors: values.tutors,
            specialisationCourses: values.specialisationCourses,
        }
        console.log("tutorium", tutorium)
        setIsTutorialOfferModalVisible(false);
        form.resetFields();
    };

    const onCancel = () => {
        setIsTutorialOfferModalVisible(false);
        form.resetFields();
    }

    useEffect(() => {
        // initial opening of page: get available courses
        if (isModalVisible) {
            getCoursesWithTitleAndSpecialisations().then(courses => {
                setCourses(courses);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });

            getUsersWithNameAndMailAndId().then(users => {
                setUsers(users);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });
        }
    }, [isModalVisible]);

    const mailPatternStudent = /^s[0-9]{6}@student\.dhbw-mannheim\.de$/g;
    const mailPatternOthers = /^[a-z]*\.[a-z]*@dhbw-mannheim\.de$/g;

    const validateTutorEmails = (rule: any, value: string[], callback: any) => {
        if (!value) {
            callback("Pflichtfeld");
            return;
        }
        value.forEach(element => {
            element = element.trim().toLowerCase();
            if (!!!(element.match(mailPatternStudent)
                || element.match(mailPatternOthers))) {
                callback("Ungültiges Format");
                return;
            }
            callback();
            return;
        });

    };

    const validateDuration = (rule: any, value: String, callback: any) => {
        console.log("value", value)
        const durationPattern = /^\d{1,2}:\d{2}$/g

        if (!value) {
            callback("Pflichtfeld");
            return;
        }
        if (value.match(durationPattern)) {
            const hours = parseInt(value.split(":")[0]);
            const minutes = parseInt(value.split(":")[1]);
            if(minutes < 60 && !(minutes === 0 && hours === 0)){
                callback();
                return;
            }
        }
        callback("Ungültiges Format");
        return;
    };



    return (

        <Modal
            destroyOnClose={true}
            visible={isModalVisible}
            onCancel={onCancel}
            title={"Tutorium erstellen"}
            width={900}
            footer={[
                <Button
                    type="primary"
                    htmlType="submit"
                    onClick={e => form.submit()}>
                    Erstellen
                </Button>
            ]}
        >

            <div>
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
                        label="Gestamumfang:"
                        name="durationMinutes"
                        rules={[{
                            required: true,
                            validator: validateDuration
                        }]}
                    >
                        <Input
                            placeholder="HH:MM"
                        />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Studiengänge:"
                        name="specialisationCourses"

                    >
                        <TreeSelect
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
                    <Form.Item
                        label="Tutoren:"
                        name="tutors"
                        rules={[{
                            required: true,
                            validator: validateTutorEmails
                        }]}
                    >
                        <Select mode="tags" tokenSeparators={[" ", ","]} placeholder="Bitte auswählen oder Adresse einfügen" >
                            {users.map(user => (
                                <Select.Option key={user.id} value={user.email}>
                                    {user.firstName} {user.lastName} ({user.email})
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </div>

        </Modal>
    )
}

export default TutorialCreateModal;
import { Button, DatePicker, Divider, Form, Input, message, Modal, Select } from "antd"
import { useForm } from "antd/lib/form/Form";
import { useEffect, useState } from "react";
import { getCoursesWithTitleAndLeaders, getRequestError } from "../../api/api";
import { CourseWithTitleAndLeaders } from "../../types/Course";
import TextArea from "antd/lib/input/TextArea";
import moment from "moment";
import { User } from "../../types/User";
import { getErrorMessageString } from "../../types/RequestError";

interface Props {
    isModalVisible: boolean,
    setIsTutorialOfferModalVisible: (visible: boolean) => void
}

const TutorialOfferModal: React.FC<Props> = ({ isModalVisible, setIsTutorialOfferModalVisible }) => {

    const [form] = useForm();
    const [courses, setCourses] = useState<CourseWithTitleAndLeaders[]>([]);

    const onFinish = (values: any) => {
        const mailBodyString = `Name:${values.firstname} ${values.lastname}%0D%0A
        Hochschule/Universität: ${values.university}%0D%0A
        Studiengang: ${values.ownCourse}%0D%0A
        Semester: ${values.semester}%0D%0A
        Zeitraum: ${moment(values.timerange[0]).format("DD.MM.YYYY")} bis  ${values.timerange[1].format("DD.MM.YYYY")}%0D%0A
        Beschreibung: ${values.description}`;

        //get Emailadresses of the directors by the selected Courses
        const mailEmailsString = values.offeredCourses.map((course: String) => courses.find(innerCourse => course === innerCourse.title)?.leadBy.map((user: User) => user.email)).flat().join(";");
        window.location.href = "mailto:" + mailEmailsString + "?body=" + mailBodyString;
        setIsTutorialOfferModalVisible(false);
        form.resetFields();
    };

    const onCancel = () => {
        setIsTutorialOfferModalVisible(false)
    }

    useEffect(() => {
        // initial opening of page: get available courses
        if (isModalVisible) {
            getCoursesWithTitleAndLeaders().then(Courses => {
                setCourses(Courses);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });
        }
    }, [isModalVisible]);



    return (

        <Modal
            visible={isModalVisible}
            onCancel={onCancel}
            title={"Tutoriumsangebot erstellen"}
            width={900}
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
                <Form
                    form={form}
                    labelWrap
                    labelCol={{ span: 9 }}
                    wrapperCol={{ span: 15 }}
                    onFinish={onFinish}
                >
                    <Divider>Persönliche Daten</Divider>
                    <Form.Item
                        rules={[{ required: true }]}
                        label="Vorname:"
                        name="firstname"
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Nachname:"
                        name="lastname"
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Hochschule / Universität:"
                        name="university"
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Studiengang:"
                        name="ownCourse"
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Semester:"
                        name="semester"
                    >
                        <Select
                            showSearch
                            placeholder="Semester wählen"
                        >
                            {Array.from(Array(12).keys()).map(i => (
                                <Select.Option key={i + 1} value={i + 1}>
                                    {i + 1}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Divider>Daten für angebotene Tutorien</Divider>
                    <Form.Item
                        label="Zeitraum:"
                        name="timerange"
                        rules={[{ required: true }]}
                    >
                        <DatePicker.RangePicker
                            placeholder={["Anfang", "Ende"]}
                            format="DD.MM.YYYY"
                            disabledDate={(current) => current && current < moment().startOf('day')}
                        />
                    </Form.Item>
                    <Form.Item
                        rules={[{ required: true }]}
                        label="Angebotene Studiengänge:"
                        name="offeredCourses"

                    >
                        <Select
                            mode="multiple"
                            showSearch
                            placeholder="Studiengang wählen"
                        >
                            {courses.map(course => (
                                <Select.Option key={course.id} value={course.title}>
                                    {course.title}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        label="Beschreibung:"
                        name="description"
                        rules={[{ required: true }]}
                    >
                        <TextArea rows={4} placeholder="Maximal 500 Zeichen" maxLength={500} showCount />
                    </Form.Item>
                </Form>





            </div>

        </Modal>
    )
}

export default TutorialOfferModal;
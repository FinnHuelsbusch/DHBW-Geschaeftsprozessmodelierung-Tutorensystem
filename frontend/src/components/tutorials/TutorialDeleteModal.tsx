import { Button, Form, message, Modal } from "antd";
import { useForm } from "antd/lib/form/Form";
import TextArea from "antd/lib/input/TextArea";
import { useState } from "react";
import { useNavigate } from "react-router";
import { deleteTutorial, getRequestError } from "../../api/api";
import { getErrorMessageString } from "../../types/RequestError";
import { Tutorial } from "../../types/Tutorial";


interface Props {
    isModalVisible: boolean,
    setIsTutorialDeleteModalVisible: (visible: boolean, deleted: boolean) => void,
    tutorial: Tutorial
}

const TutorialDeleteModal: React.FC<Props> = ({ isModalVisible, setIsTutorialDeleteModalVisible, tutorial }) => {

    const [form] = useForm();
    const [loading, setLoading] = useState(false);

    const onFinish = (values: any) => {
        setLoading(true);
        deleteTutorial(tutorial.id, values.reason).then(res => {
            message.success("Löschen erfolgreich");
            setIsTutorialDeleteModalVisible(false, true);
            form.resetFields();
            setLoading(false);
        }).catch(err => {
            const reqErr = getRequestError(err);
            message.error(getErrorMessageString(reqErr.errorCode));
            setLoading(false);
        });
    };

    const onCancel = () => {
        setIsTutorialDeleteModalVisible(false, false)
    }

    return (
        <Modal
            visible={isModalVisible}
            onCancel={onCancel}
            title={"Tutorium löschen"}
            width={900}
            footer={[
                <Button
                    danger
                    type="primary"
                    htmlType="submit"
                    disabled={loading}
                    onClick={e => form.submit()}>
                    Löschen
                </Button>
            ]}
        >

            <Form
                form={form}
                labelWrap
                labelCol={{ span: 2 }}
                wrapperCol={{ span: 22 }}
                onFinish={onFinish}
            >

                <Form.Item
                    label="Grund:"
                    name="reason"
                >
                    <TextArea
                        rows={4}
                        placeholder='Bei Bedarf, kann ein Grund angegeben werden.  Wird kein Grund angegeben werden die Studierenden und die Tutoren nur über die Absage informiert.'
                        maxLength={500}
                        disabled={loading}
                        showCount />
                </Form.Item>

            </Form>

        </Modal>
    )
}

export default TutorialDeleteModal; 
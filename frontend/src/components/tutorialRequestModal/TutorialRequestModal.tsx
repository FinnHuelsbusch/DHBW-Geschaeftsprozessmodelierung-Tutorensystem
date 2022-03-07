import { Form, Modal, Input, Button, message } from 'antd';
import { useForm } from "antd/lib/form/Form";
import { useState } from "react";
import { createTutorialRequest } from "../../api/api";
import { TutorialRequest } from "../../types/Tutorial";

interface Props {
    isModalVisible: boolean,
    setIsTutorialRequestModalVisible: (visible: boolean) => void
}



export const TutorialRequestModal: React.FC<Props> = ({ isModalVisible, setIsTutorialRequestModalVisible }) => {

    const [loading, setLoading] = useState(false);
    const [form] = useForm();

    const onFinish = (values: any) => {
        setLoading(true);
        createTutorialRequest({
            description: values.module
        } as TutorialRequest).then(res => {
            setLoading(false);
            message.success("Tutoriumsanfrage erfolgreich erstellt.");
            setIsTutorialRequestModalVisible(false);
            form.resetFields()
        }, err => {
            setLoading(false);
            message.error("Tutoriumsanfrage konnte nicht erstellt werden.");
        }

        )
    };

    const onCancel = () => {
        setIsTutorialRequestModalVisible(false)
        form.resetFields()
    }

    return (
        <Modal
            title="Tutoriumsanfrage erstellen"
            destroyOnClose={true}
            visible={isModalVisible}
            onCancel={onCancel}
            width={600}
            footer={[
                <Button loading={loading} htmlType="submit" type="primary" onClick={e => form.submit()}>
                    Absenden
                </Button>
            ]}>
            <Form
                onFinish={onFinish}
                form={form}
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 17 }}
            >
                <Form.Item label="Modul" name="module" rules={[{ required: true },{whitespace: true}]}>
                    <Input placeholder="Programmieren I" allowClear />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default TutorialRequestModal;
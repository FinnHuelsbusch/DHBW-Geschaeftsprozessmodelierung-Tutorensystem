import { Button, ConfigProvider, DatePicker, Form, message, Modal } from "antd"
import { useForm } from "antd/lib/form/Form";
import TextArea from "antd/lib/input/TextArea";
import locale from 'antd/lib/locale/de_DE';
import 'moment/locale/de'
import { useState } from "react";
import { createTutorialOffer } from "../../api/api";
import { TutorialOffer } from "../../types/Tutorial";



interface Props {
    isModalVisible: boolean,
    setIsTutorialOfferModalVisible: (visible: boolean) => void
}


const TutorialOfferModal: React.FC<Props> = ({ isModalVisible, setIsTutorialOfferModalVisible }) => {

    const [loading, setLoading] = useState(false);
    const [form] = useForm(); 

    const onFinish = (values: any) => {
        setLoading(true);
        createTutorialOffer({
            description : values.description,
            start: values.timerange[0].toDate(), 
            end: values.timerange[1].toDate()
        } as TutorialOffer).then( res => 
            {
                setLoading(false); 
                message.success("Tutorumsangebot erfolgreich erstellt."); 
                setIsTutorialOfferModalVisible(false); 
            }, err => {
                setLoading(false); 
                message.error("Tutorumsangebot konnte nicht erstellt werden."); 
            }
            
        )
    };

    return (
        <ConfigProvider locale={locale}>
            <Modal
                destroyOnClose={true}
                visible={isModalVisible}
                footer={null}
                onCancel={() => setIsTutorialOfferModalVisible(false)}
            >
                <Form
                    onFinish={onFinish}
                    form={form}
                >
                    <Form.Item
                        label="Zeitraum"
                        name="timerange"
                    >
                        <DatePicker.RangePicker
                            placeholder={["Anfang", "Ende"]}
                            format="DD.MM.YYYY"
                        />

                    </Form.Item>
                    <Form.Item
                        label="Beschreibung"
                        name="description"
                    >
                        <TextArea rows={4} placeholder="Maximal 500 Zeichen" maxLength={500} />
                    </Form.Item>

                    <Form.Item>
                        <Button loading={loading} type="primary" htmlType="submit" className="submit-form-button">
                            Absenden
                        </Button>
                    </Form.Item>
                </Form>
            </Modal>
        </ConfigProvider>
    )
}

export default TutorialOfferModal;
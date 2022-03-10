import { Col, Modal, Row, Tag, Typography } from 'antd';
import Paragraph from 'antd/lib/typography/Paragraph';
import Title from 'antd/lib/typography/Title';
import React from 'react';
import { Tutorial } from '../../types/Tutorial';
import { formatDate } from '../../utils/DateTimeHandling';
import './TutorialDetails.scss';

type Props = {
    tutorial: Tutorial | undefined,
    onClose: () => void,
}

const TutorialDetails: React.FC<Props> = ({ tutorial, onClose }) => {

    const getDetailsRow = (label: string, content: any) => {
        return (
            <Row>
                <Col className="view-only-label" flex={"0 0 200px"}>{label}</Col>
                <Col className="view-only-value" flex={"1 1 200px"}>{content}</Col>
            </Row >
        );
    }

    return (
        <Modal
            visible={tutorial ? true : false}
            onCancel={onClose}
            title={tutorial?.title}
        >
            {tutorial &&
                <Typography>
                    <Title level={4}>Details</Title>
                    <Paragraph>
                        {getDetailsRow("Gesamtumfang", `${tutorial.durationMinutes} Minuten`)}
                        {getDetailsRow("Zeitraum", `${formatDate(tutorial.start)} - ${formatDate(tutorial.end)}`)}
                        {getDetailsRow("Anzahl Teilnehmer", tutorial.numberOfParticipants)}
                    </Paragraph>

                    <Title level={4}>Inhalt</Title>
                    <Paragraph>{tutorial.description}</Paragraph>

                    <Title level={4}>Für Studiengänge</Title>
                    <Paragraph>
                        {tutorial.specialisationCourses.map(course => (
                            <Tag>{"Specialisation"}</Tag>
                        ))}
                    </Paragraph>
                </Typography>
            }
        </Modal>
    );
};

export default TutorialDetails;
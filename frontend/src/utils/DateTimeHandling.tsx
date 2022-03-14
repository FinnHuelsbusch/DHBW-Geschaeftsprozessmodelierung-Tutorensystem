import moment from "moment";

const defaultDateFormat = "DD.MM.YY";

export const formatDate = (date: Date, format: string = defaultDateFormat): string => {
    return moment(date).format(format);
}
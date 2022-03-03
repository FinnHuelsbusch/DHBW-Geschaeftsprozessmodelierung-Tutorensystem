export const validateMessages = {
    default: "Validierungsfehler im Feld '${label}'",
    required: "'${label}' muss ausgefüllt sein",
    enum: "'${label}' muss eins von [${enum}] sein",
    whitespace: "'${label}' darf nicht leer sein",
    date: {
      format: "'${label}' ist ungeeignet für ein Datum",
      parse: "'${label}' konnte nicht in ein Datum umgewandelt werden",
      invalid: "'${label}' ist ein ungültiges Datum",
    },
    string: {
      len: "'${label}'muss genau ${len} Zeichen lang sein",
      min: "'${label}' muss mindestens ${min} Zeichen lang sein",
      max: "'${label}' darf nicht länger als ${max} Zeichen sein",
      range: "'${label}' muss zwischen ${min} und ${max} Zeichen lang sein",
    },
    number: {
      len: "'${label}' muss gleich ${len} sein",
      min: "'${label}' darf nicht kleiner als ${min} sein",
      max: "'${label}' darf nicht größer als ${max} sein",
      range: "'${label}' muss zwischen ${min} und ${max} liegen",
    },
    array: {
      len: "'${label}' muss genau ${len} Elemente umfassen",
      min: "'${label}' darf nicht weniger als ${min} Elemente umfassen",
      max: "'${label}' darf nicht mehr als ${max} Elemente umfassen",
      range: "'${label}' muss zwischen ${min} und ${max} Elemente beinhalten",
    },
    pattern: {
      mismatch: "'${label}' stimmt nicht mit dem geforderten Format ${pattern} überein",
    },
};
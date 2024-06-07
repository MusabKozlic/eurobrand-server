package com.eurobrand.services;

import org.springframework.stereotype.Service;

@Service
public class NumberToWordsConverter {
    private static final String[] jedinice = {
            "", "jedan", "dva", "tri", "četiri", "pet", "šest", "sedam", "osam", "devet"
    };

    private static final String[] desetice = {
            "", "deset", "dvadeset", "trideset", "četrdeset", "pedeset",
            "šezdeset", "sedamdeset", "osamdeset", "devedeset"
    };

    private static final String[] specijalneDesetice = {
            "jedanaest", "dvanaest", "trinaest", "četrnaest", "petnaest",
            "šesnaest", "sedamnaest", "osamnaest", "devetnaest"
    };

    private static final String[] hiljade = {
            "", "hiljada", "milijun", "milijarda"
    };

    public String convertToWords(double number) {
        long intPart = (long) number;
        int decimalPart = (int) Math.round((number - intPart) * 100);

        String words = convert(intPart) + " KM";
        if (decimalPart > 0) {
            words += " i " + convert(decimalPart) + " 20/100KM";
        }

        return words;
    }

    private static String convert(long number) {
        if (number == 0) {
            return "nula";
        }

        String result = "";

        if (number < 0) {
            result += "minus ";
            number = -number;
        }

        int groupIndex = 0;

        while (number > 0) {
            if (number % 1000 != 0) {
                result = convertGroup((int) (number % 1000)) + " " + hiljade[groupIndex] + " " + result;
            }
            number /= 1000;
            groupIndex++;
        }

        return result.trim();
    }

    private static String convertGroup(int number) {
        String result = "";

        if (number >= 100) {
            result += jedinice[number / 100] + " stotina ";
            number %= 100;
        }

        if (number >= 20) {
            result += desetice[number / 10] + " ";
            number %= 10;
        }

        if (number > 10 && number < 20) {
            result += specijalneDesetice[number - 11] + " ";
            number = 0;
        }

        if (number > 0) {
            result += jedinice[number] + " ";
        }

        return result;
    }
}

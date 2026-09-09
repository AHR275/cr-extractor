package app.core.translator;

import java.util.LinkedHashMap;
import java.util.Map;

public class Translator  {

    private final Map<String, String> translations =
            new LinkedHashMap<>();

    public Translator() {

        // =========================
        // CR Information
        // =========================

        translations.put("Is Restricted", "مقيد");
        translations.put("CR National Number", "الرقم الوطني الموحد");
        translations.put("CR Number", "رقم السجل التجاري");
        translations.put(
                "Encrypted CR Number",
                "الرقم المشفر للسجل التجاري"
        );

        translations.put("Owner Name", "اسم المالك");
        translations.put(
                "Owner Identifier Number",
                "رقم هوية المالك"
        );
        translations.put(
                "Owner Identifier Type Name",
                "اسم نوع هوية المالك"
        );

        translations.put("Version No", "رقم الإصدار");

        translations.put(
                "Entity Full Name",
                "الاسم الكامل للمنشأة"
        );

        translations.put("Capital", "رأس المال");
        translations.put("Company Duration", "مدة الشركة");
        translations.put("Is Main", "رئيسي");

        translations.put(
                "Main CR National Number",
                "الرقم الوطني الموحد للسجل الرئيسي"
        );

        translations.put(
                "Main CR Number",
                "رقم السجل التجاري الرئيسي"
        );

        translations.put(
                "Is License Based",
                "قائم على ترخيص"
        );

        translations.put(
                "In Liquidation Process",
                "تحت التصفية"
        );

        translations.put(
                "Has Ecommerce",
                "لديه تجارة إلكترونية"
        );

        translations.put("Is Listed", "مدرج");

        translations.put(
                "Entity Name Lang",
                "لغة اسم المنشأة"
        );

        translations.put(
                "Entity Name Lang ID",
                "معرف لغة اسم المنشأة"
        );

        translations.put(
                "Entity Name Lang Desc",
                "وصف لغة اسم المنشأة"
        );

        translations.put(
                "Entity Type",
                "نوع المنشأة"
        );

        translations.put(
                "Entity Type ID",
                "معرف نوع المنشأة"
        );

        translations.put(
                "Entity Type Desc",
                "وصف نوع المنشأة"
        );

        translations.put(
                "Capital Currency",
                "عملة رأس المال"
        );

        translations.put(
                "Capital Currency ID",
                "معرف عملة رأس المال"
        );

        translations.put(
                "Capital Currency Desc",
                "وصف عملة رأس المال"
        );

        translations.put(
                "Company Form",
                "الشكل القانوني للشركة"
        );

        translations.put(
                "Company Form ID",
                "معرف الشكل القانوني للشركة"
        );

        translations.put(
                "Company Form Description",
                "وصف الشكل القانوني للشركة"
        );

        translations.put(
                "Company Character List",
                "خصائص الشركة"
        );

        translations.put(
                "Company Character ID",
                "معرف خاصية الشركة"
        );

        translations.put(
                "Company Character Description",
                "وصف خاصية الشركة"
        );

        translations.put(
                "Partners Nationality",
                "جنسية الشركاء"
        );

        translations.put(
                "Partners Nationality ID",
                "معرف جنسية الشركاء"
        );

        translations.put(
                "Partners Nationality Desc",
                "وصف جنسية الشركاء"
        );

        translations.put(
                "Last Modification Date",
                "تاريخ آخر تعديل"
        );

        translations.put(
                "Last Modification Date Gregorian",
                "تاريخ آخر تعديل الميلادي"
        );

        translations.put(
                "Last Modification Date Hijri",
                "تاريخ آخر تعديل الهجري"
        );

        translations.put(
                "CR Issue Date",
                "تاريخ إصدار السجل التجاري"
        );

        translations.put(
                "CR Issue Date Gregorian",
                "تاريخ إصدار السجل التجاري الميلادي"
        );

        translations.put(
                "CR Issue Date Hijri",
                "تاريخ إصدار السجل التجاري الهجري"
        );

        translations.put(
                "CR Confirm Date",
                "تاريخ تأكيد السجل التجاري"
        );

        translations.put(
                "CR Confirm Date Gregorian",
                "تاريخ تأكيد السجل التجاري الميلادي"
        );

        translations.put(
                "CR Confirm Date Hijri",
                "تاريخ تأكيد السجل التجاري الهجري"
        );

        translations.put(
                "CR Status",
                "حالة السجل التجاري"
        );

        translations.put(
                "CR Status ID",
                "معرف حالة السجل التجاري"
        );

        translations.put(
                "CR Status Desc",
                "وصف حالة السجل التجاري"
        );

        translations.put(
                "CR Delete Date",
                "تاريخ حذف السجل التجاري"
        );

        translations.put(
                "CR Delete Reasons",
                "أسباب حذف السجل التجاري"
        );


        // =========================
        // CR Activities
        // =========================

        translations.put(
                "Full Activities Text",
                "النص الكامل للأنشطة"
        );

        translations.put(
                "Activities Type",
                "نوع الأنشطة"
        );

        translations.put(
                "Activities Type ID",
                "معرف نوع الأنشطة"
        );

        translations.put(
                "Activities Type Description",
                "وصف نوع الأنشطة"
        );

        translations.put(
                "Activity List",
                "قائمة الأنشطة"
        );

        translations.put(
                "Activity ID",
                "معرف النشاط"
        );

        translations.put(
                "Activity Description",
                "وصف النشاط"
        );

        translations.put(
                "Is Pre License Issued",
                "تم إصدار الترخيص المسبق"
        );

        translations.put(
                "Is Post License Issued",
                "تم إصدار الترخيص اللاحق"
        );


        // =========================
        // Entity Address
        // =========================

        translations.put(
                "Address Type",
                "نوع العنوان"
        );

        translations.put(
                "Address Type ID",
                "معرف نوع العنوان"
        );

        translations.put(
                "Address Type Description",
                "وصف نوع العنوان"
        );

        translations.put(
                "National Address",
                "العنوان الوطني"
        );

        translations.put(
                "City",
                "المدينة"
        );

        translations.put(
                "City ID",
                "معرف المدينة"
        );

        translations.put(
                "City Name",
                "اسم المدينة"
        );

        translations.put(
                "District",
                "الحي"
        );

        translations.put(
                "District ID",
                "معرف الحي"
        );

        translations.put(
                "District Name",
                "اسم الحي"
        );

        translations.put(
                "Street Name",
                "اسم الشارع"
        );

        translations.put(
                "Building No",
                "رقم المبنى"
        );

        translations.put(
                "Unit No",
                "رقم الوحدة"
        );

        translations.put(
                "Postal Code",
                "الرمز البريدي"
        );

        translations.put(
                "Additional No",
                "الرقم الإضافي"
        );

        translations.put(
                "Full Address",
                "العنوان الكامل"
        );


        // =========================
        // Capital Details
        // =========================

        translations.put(
                "Contribution Capital Details",
                "تفاصيل رأس مال المساهمة"
        );

        translations.put(
                "Capital Type",
                "نوع رأس المال"
        );

        translations.put(
                "Capital Type ID",
                "معرف نوع رأس المال"
        );

        translations.put(
                "Capital Type Description",
                "وصف نوع رأس المال"
        );

        translations.put(
                "Cash Capital",
                "رأس المال النقدي"
        );

        translations.put(
                "Inkind Capital",
                "رأس المال العيني"
        );

        translations.put(
                "Total Cash Contribution",
                "إجمالي المساهمة النقدية"
        );

        translations.put(
                "Total Inkind Contribution",
                "إجمالي المساهمة العينية"
        );

        translations.put(
                "Contribution Value",
                "قيمة المساهمة"
        );

        translations.put(
                "Stock Capital Details",
                "تفاصيل رأس مال الأسهم"
        );

        translations.put(
                "Stock List",
                "قائمة الأسهم"
        );


        // =========================
        // Contact Information
        // =========================

        translations.put(
                "Phone No",
                "رقم الهاتف"
        );

        translations.put(
                "Mobile No",
                "رقم الجوال"
        );

        translations.put(
                "Email",
                "البريد الإلكتروني"
        );

        translations.put(
                "Website URL",
                "رابط الموقع الإلكتروني"
        );


        // =========================
        // Management Information
        // =========================

        translations.put(
                "Mangment Information",
                "معلومات الإدارة"
        );

        translations.put(
                "Management Structure",
                "هيكل الإدارة"
        );

        translations.put(
                "Management Structure ID",
                "معرف هيكل الإدارة"
        );

        translations.put(
                "Management Structure Description",
                "وصف هيكل الإدارة"
        );

        translations.put(
                "Manager List",
                "قائمة المديرين"
        );

        translations.put(
                "Manager Ref",
                "مرجع المدير"
        );

        translations.put(
                "Identifier Type ID",
                "معرف نوع الهوية"
        );

        translations.put(
                "Identifier Number",
                "رقم الهوية"
        );

        translations.put(
                "Nationality ID",
                "معرف الجنسية"
        );

        translations.put(
                "Is Licensed",
                "مرخص"
        );

        translations.put(
                "Manager Type",
                "نوع المدير"
        );

        translations.put(
                "Manager Type ID",
                "معرف نوع المدير"
        );

        translations.put(
                "Manager Type Description",
                "وصف نوع المدير"
        );


        // =========================
        // Person Information
        // =========================

        translations.put(
                "Person Info",
                "معلومات الشخص"
        );

        translations.put(
                "Identifier No",
                "رقم الهوية"
        );

        translations.put(
                "Title",
                "اللقب"
        );

        translations.put(
                "First Name",
                "الاسم الأول"
        );

        translations.put(
                "Father Name",
                "اسم الأب"
        );

        translations.put(
                "Grand Father Name",
                "اسم الجد"
        );

        translations.put(
                "Family Name",
                "اسم العائلة"
        );

        translations.put(
                "Identifier Type",
                "نوع الهوية"
        );

        translations.put(
                "Identifier Type ID",
                "معرف نوع الهوية"
        );

        translations.put(
                "Identifier Type Desc",
                "وصف نوع الهوية"
        );

        translations.put(
                "Nationality",
                "الجنسية"
        );

        translations.put(
                "Nationality Description",
                "وصف الجنسية"
        );

        translations.put(
                "Is Saudi",
                "سعودي"
        );

        translations.put(
                "Is Gulf",
                "خليجي"
        );


        // =========================
        // Manager Position
        // =========================

        translations.put(
                "Manager Position List",
                "قائمة المناصب الإدارية"
        );

        translations.put(
                "Manager Position ID",
                "معرف المنصب الإداري"
        );

        translations.put(
                "Manager Position Description",
                "وصف المنصب الإداري"
        );


        // =========================
        // Liquidators
        // =========================

        translations.put(
                "Liquidator List",
                "قائمة المصفين"
        );

        translations.put(
    "Entity Name Lang Description",
    "وصف لغة اسم المنشأة"
);

translations.put(
    "Entity Type Description",
    "وصف نوع المنشأة"
);

translations.put(
    "Capital Currency Description",
    "وصف عملة رأس المال"
);

translations.put(
    "Company Form Description",
    "وصف الشكل القانوني للشركة"
);

translations.put(
    "Company Character Description",
    "وصف خاصية الشركة"
);

translations.put(
    "Partners Nationality Description",
    "وصف جنسية الشركاء"
);

translations.put(
    "CR Status Description",
    "وصف حالة السجل التجاري"
);
    }

    
    public String translate(String english) {
        return translations.get(english);
    }
}
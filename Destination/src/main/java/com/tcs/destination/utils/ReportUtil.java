package com.tcs.destination.utils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.PenBuilder;
import net.sf.dynamicreports.report.builder.style.SimpleStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.SplitType;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.collections.CollectionUtils;

import com.tcs.destination.config.ReportDataItem;

/**
 * Utility for generating the reports
 * 
 * @author TCS
 *
 */
public class ReportUtil {

	public static final String FONT_MYRIAD_PRO = "Myriad Pro";
	public static final Integer FONT_SIZE_NORMAL = 10;
	public static final Integer FONT_SIZE_HEADER = 12;
	public static final Integer FONT_SIZE_TITLE = 14;
	public static final Integer FONT_SIZE_SUB_TITLE = 12;

	/**
	 * Method used to build the report with the data source, columns , title
	 * provided
	 * 
	 * @param columns
	 * @param title
	 * @param datasource
	 * @return
	 */
	public static JasperReportBuilder build(ColumnBuilder<?, ?>[] columns,
			TextFieldBuilder<String> title, JRDataSource datasource) {

		PenBuilder borderStyle = DynamicReports.stl.penThin()
				.setLineColor(new Color(219, 225, 232)).setLineWidth(1f);
		FontBuilder defaultFont = DynamicReports.stl.font().setFontName(FONT_MYRIAD_PRO);

		StyleBuilder titleStyle = DynamicReports.stl.style().setFontSize(FONT_SIZE_TITLE)
				.setLineSpacingSize(10f).setBold(false);
		title.setStyle(titleStyle);
		
		StyleBuilder subTitleStyle = DynamicReports.stl.style().setFontSize(FONT_SIZE_SUB_TITLE).italic().setBold(false);

		StyleBuilder headerStyle = DynamicReports.stl
				.style()
				.setTextAlignment(HorizontalTextAlignment.LEFT,
						VerticalTextAlignment.MIDDLE)
				.setBorder(DynamicReports.stl.penThin())
				.setBackgroundColor(new Color(4, 125, 196))
				// #007DC5
				.setForegroundColor(Color.WHITE).setPadding(10)
				.setFontSize(FONT_SIZE_HEADER);

		SimpleStyleBuilder highlightStyle = DynamicReports.stl.simpleStyle()
				.setBackgroundColor(new Color(246, 247, 247));

		StyleBuilder columnStyle = DynamicReports.stl
				.style()
				.setTextAlignment(HorizontalTextAlignment.LEFT,
						VerticalTextAlignment.TOP).setLeftBorder(borderStyle)
				.setRightBorder(borderStyle).setTopBorder(borderStyle)
				.setBottomBorder(borderStyle).setPadding(5).setFontSize(FONT_SIZE_NORMAL);

		return DynamicReports
				.report()
				.setDefaultFont(defaultFont)
				.title(title,
						DynamicReports.cmp.verticalGap(5),
						DynamicReports.cmp.text("Details:").setStyle(
								subTitleStyle),
						DynamicReports.cmp.verticalGap(5)).columns(columns)
				.setColumnTitleStyle(headerStyle).setColumnStyle(columnStyle)
				.setDataSource(datasource)
				.setDetailSplitType(SplitType.PREVENT).highlightDetailOddRows()
				.setDetailOddRowStyle(highlightStyle);
	}

	/**
	 * Creates a data source to be used for generating the reports
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static JRDataSource createDataSource(List<?> data) throws Exception {
		return new JRBeanCollectionDataSource(data);
	}

	/**
	 * creates a data source with the data given and constructing the columns
	 * and builds the report
	 * 
	 * @param title
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static JasperReportBuilder buildReport(
			TextFieldBuilder<String> title, List<?> data) throws Exception {
		JasperReportBuilder report = null;
		int width = 40;
		int height = 40;
		if (CollectionUtils.isNotEmpty(data)) {
			Object obj = data.get(0);
			Field[] fields = obj.getClass().getDeclaredFields();
			TextColumnBuilder<?>[] columns = new TextColumnBuilder[fields.length];
			int i = 0;
			for (Field field : fields) {
				String columnName = null;
				if (field.isAnnotationPresent(ReportDataItem.class)) {
					ReportDataItem item = field
							.getAnnotation(ReportDataItem.class);
					columnName = item.columnName();
					width = item.width();
					height = item.height();
				} else {
					columnName = field.getName();
				}

				columns[i] = Columns
						.column(columnName, field.getName(), field.getType())
						.setStretchWithOverflow(true).setWidth(width)
						.setHeight(height);
				i++;
			}

			JRDataSource dataSource = createDataSource(data);
			report = build(columns, title, dataSource);

		} else {
			// Building the report with title alone if data is not available
			report = build(title);
		}

		return report;
	}

	/**
	 * Method used to build the report with the title alone
	 * 
	 * @param title
	 * @return
	 */
	private static JasperReportBuilder build(TextFieldBuilder<String> title) {
		FontBuilder fontStyle = DynamicReports.stl.font().setFontName(FONT_MYRIAD_PRO).setFontSize(FONT_SIZE_TITLE);
		return DynamicReports.report().setDefaultFont(fontStyle).title(title);
	}

}
package org.grails.taglib

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.util.TypeConvertingMap
import grails.web.mapping.UrlMapping
import groovy.transform.CompileStatic

import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat

import org.grails.plugins.web.GrailsTagDateHelper
import org.grails.taglib.TagOutput
import org.grails.taglib.encoder.OutputContextLookupHelper
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.servlet.support.RequestContextUtils as RCU
import org.springframework.web.servlet.support.RequestDataValueProcessor

class BootstrapTagLib implements ApplicationContextAware, InitializingBean, GrailsConfigurationAware  {
    // static defaultEncodeAs = [taglib:'none']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

	static namespace = "bs"

    ApplicationContext applicationContext
    RequestDataValueProcessor requestDataValueProcessor
    GrailsTagDateHelper grailsTagDateHelper

    private List<String> booleanAttributes = ['disabled', 'checked', 'readonly','required']
    private static final def PRECISION_RANKINGS = ["year": 0, "month": 10, "day": 20, "hour": 30, "minute": 40, "second": 50]
    private static final def DEFAULT_CSS_CLASSES = ['year': 'year', 'month': 'month', 'day': 'day', 'hour': 'hour', 'minute': 'minute', 'second': 'second']

    void afterPropertiesSet() {
        if (applicationContext.containsBean('requestDataValueProcessor')) {
            requestDataValueProcessor = applicationContext.getBean('requestDataValueProcessor', RequestDataValueProcessor)
        }
    }

    /**
     * A simple date picker that renders a date as selects.<br/>
     * e.g. &lt;g:datePicker name="myDate" value="${new Date()}" /&gt;
     *
     * @emptyTag
     *
     * @attr name REQUIRED The name of the date picker field set
     * @attr value The current value of the date picker; defaults to either the value specified by the default attribute or now if no default is set
     * @attr default A Date or parsable date string that will be used if there is no value
     * @attr precision The desired granularity of the date to be rendered
     * @attr cssClasses The css styles of the select tags
     * @attr order Set to an array containing 'day', 'month' and 'year' to customize the order in which the select fields are shown. Defaults to the order defined in the respective locale (e.g. ['month', 'day', 'year'] in the en locale that ships with Grails).
     * @attr noSelection A single-entry map detailing the key and value to use for the "no selection made" choice in the select box. If there is no current selection this will be shown as it is first in the list, and if submitted with this selected, the key that you provide will be submitted. Typically this will be blank.
     * @attr years A list or range of years to display, in the order specified. i.e. specify 2007..1900 for a reverse order list going back to 1900. If this attribute is not specified, a range of years from the current year - 100 to current year + 100 will be shown.
     * @attr relativeYears A range of int representing values relative to value. For example, a relativeYears of -2..7 and a value of today will render a list of 10 years starting with 2 years ago through 7 years in the future. This can be useful for things like credit card expiration dates or birthdates which should be bound relative to today.
     * @attr id the DOM element id
     * @attr disabled Makes the resulting inputs and selects to be disabled. Is treated as a Groovy Truth.
     * @attr readonly Makes the resulting inputs and selects to be made read only. Is treated as a Groovy Truth.
     */
    Closure datePicker = { attrs ->
        def name = attrs.name
        def id = attrs.id ?: name
        def order = attrs.order ?: []

        if (order) {
            def forbidden = order - ['year', 'month', 'day']
            if (forbidden) {
                throwTagError("Tag [datePicker].order only accepts 'year', 'month', 'day'")
            }
        }
        else {
            def formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT, RCU.getLocale(request))
            def pattern = formatter.toPattern()

            def letters = []
            if (pattern.indexOf('/') > 0) {
                letters = pattern.tokenize('/')
            }
            else if (pattern.indexOf('.') > 0) {
                letters = pattern.tokenize('.')
            }
            else if (pattern.indexOf('-') > 0) {
                letters = pattern.tokenize('-')
            }
            else {
                letters = pattern.tokenize()
            }

            def mapping = ['year': ['y', 'yy', 'yyyy'], 'month': ['M', 'MM'], 'day': ['d', 'dd']]
            letters.each { letter ->
                mapping.each { k, v ->
                    if (letter in v) order << k
                }
            }
        }

        def precision = (attrs.precision ? PRECISION_RANKINGS[attrs.precision] :
            (grailsApplication.config.grails.tags.datePicker.default.precision ?
                PRECISION_RANKINGS["${grailsApplication.config.grails.tags.datePicker.default.precision}"] :
                PRECISION_RANKINGS["minute"]))

        booleanToAttribute(attrs, 'disabled')
        booleanToAttribute(attrs, 'readonly')

        // Change this hidden to use requestDataValueProcessor
        def dateStructValue = processFormFieldValueIfNecessary("${name}", "date.struct", "hidden")
        out.println "<input type=\"hidden\" name=\"${name}\" value=\"${dateStructValue}\" />"

        order += ['hour', 'minute', 'second']

        order.each {
            if (precision >= PRECISION_RANKINGS[it]) {
                out.println("<label style=\"display:none;\" for=\"${name}_${it}\" id=\"label_${name}_${it}\">${it.capitalize()}</label>")
                "select${it.capitalize()}"(out, attrs)
            }
        }
    }

    Closure timePicker = { attrs ->
        def name = attrs.name
        def id = attrs.id ?: name

        booleanToAttribute(attrs, 'disabled')
        booleanToAttribute(attrs, 'readonly')

        // Change this hidden to use requestDataValueProcessor
        def dateStructValue = processFormFieldValueIfNecessary("${name}", "date.struct", "hidden")
        out.println "<input type=\"hidden\" name=\"${name}\" value=\"${dateStructValue}\" />"

        ['hour', 'minute', 'second'].each {
            out.println("<label style=\"display:none;\" for=\"${name}_${it}\" id=\"label_${name}_${it}\">${it.capitalize()}</label>")
            "select${it.capitalize()}"(out, attrs)
        }
    }

    def selectYear(out, attrs) {
        def xdefault = attrs['default']
        if (xdefault == null) {
            xdefault = new Date()
        }
        else if (xdefault.toString() != 'none') {
            if (xdefault instanceof String) {
                xdefault = DateFormat.getInstance().parse(xdefault)

            }
            else if (!grailsTagDateHelper.supportsDatePicker(xdefault.class)) {
                throwTagError("Tag [datePicker] the default date is not a supported class")
            }
        }
        else {
            xdefault = null
        }

        def name = attrs.name
        def id = attrs.id ?: name
        def cssClasses = attrs.cssClasses

        if (cssClasses == 'true') {
            cssClasses = DEFAULT_CSS_CLASSES
        }

        def value = attrs.value
        if (value.toString() == 'none') {
            value = null
        }
        else if (!value) {
            value = xdefault
        }

        def c = null
        if (value instanceof Calendar) {
            c = value
        }
        else if (value != null) {
            c = grailsTagDateHelper.buildCalendar(value)
        }

        def year
        if (c != null) {
            year = c.get(GregorianCalendar.YEAR)
        }

        def noSelection = attrs.noSelection
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        def years = attrs.years
        def relativeYears = attrs.relativeYears
        if (years != null && relativeYears != null) {
            throwTagError 'Tag [datePicker] does not allow both the years and relativeYears attributes to be used together.'
        }

        if (relativeYears != null) {
            if (!(relativeYears instanceof IntRange)) {
                // allow for a syntax like relativeYears="[-2..5]".  The value there is a List containing an IntRage.
                if ((!(relativeYears instanceof List)) || (relativeYears.size() != 1) || (!(relativeYears[0] instanceof IntRange))) {
                    throwTagError 'The [datePicker] relativeYears attribute must be a range of int.'
                }
                relativeYears = relativeYears[0]
            }
        }
    
        if (years == null) {
            def tempyear
            if (year == null) {
                // If no year, we need to get current year to setup a default range... ugly
                def tempc = new GregorianCalendar()
                tempc.setTime(new Date())
                tempyear = tempc.get(GregorianCalendar.YEAR)
            }
            else {
                tempyear = year
            }
            if (relativeYears) {
                if (relativeYears.reverse) {
                    years = (tempyear + relativeYears.toInt)..(tempyear + relativeYears.fromInt)
                } else {
                    years = (tempyear + relativeYears.fromInt)..(tempyear + relativeYears.toInt)
                }
            } else {
                years = (tempyear + 100)..(tempyear - 100)
            }
        }

        out.println "<select name=\"${name}_year\" id=\"${id}_year\" aria-labelledby=\"${name} ${name}_year\""
        if (attrs.disabled) {
            out << ' disabled="disabled"'
        }
        if (attrs.readonly) {
            out << ' readonly="readonly"'
        }
        if (cssClasses) {
            out << "class=\"" + cssClasses['year'] + "\""
        }
        out << '>'

        if (noSelection) {
            renderNoSelectionOptionImpl(out, noSelection.key, noSelection.value, '')
            out.println()
        }

        for (i in years) {
            // Change this year option to use requestDataValueProcessor
            def yearIndex  = processFormFieldValueIfNecessary("${name}_year","${i}","option")
            out.println "<option value=\"${yearIndex}\"${i == year ? ' selected="selected"' : ''}>${i}</option>"
        }
        out.println '</select>'
    }

    def selectMonth(out, attrs) {
        def xdefault = attrs['default']
        if (xdefault == null) {
            xdefault = new Date()
        }
        else if (xdefault.toString() != 'none') {
            if (xdefault instanceof String) {
                xdefault = DateFormat.getInstance().parse(xdefault)

            }
            else if (!grailsTagDateHelper.supportsDatePicker(xdefault.class)) {
                throwTagError("Tag [datePicker] the default date is not a supported class")
            }
        }
        else {
            xdefault = null
        }

        def name = attrs.name
        def id = attrs.id ?: name
        def cssClasses = attrs.cssClasses

        if (cssClasses == 'true') {
            cssClasses = DEFAULT_CSS_CLASSES
        }

        def value = attrs.value
        if (value.toString() == 'none') {
            value = null
        }
        else if (!value) {
            value = xdefault
        }

        def c = null
        if (value instanceof Calendar) {
            c = value
        }
        else if (value != null) {
            c = grailsTagDateHelper.buildCalendar(value)
        }

        def month
        if (c != null) {
            month = c.get(GregorianCalendar.MONTH)
        }

        def noSelection = attrs.noSelection
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        out.println "<select name=\"${name}_month\" id=\"${id}_month\" aria-labelledby=\"${name} ${name}_month\""
        if (attrs.disabled) {
            out << ' disabled="disabled"'
        }
        if (attrs.readonly) {
            out << ' readonly="readonly"'
        }
        if (cssClasses) {
            out << "class=\"" + cssClasses['month'] + "\""
        }
        out << '>'

        if (noSelection) {
            renderNoSelectionOptionImpl(out, noSelection.key, noSelection.value, '')
            out.println()
        }

        def dfs = new DateFormatSymbols(RCU.getLocale(request))
        dfs.months.eachWithIndex {m, i ->
            if (m) {
                def monthIndex = i + 1
                monthIndex = processFormFieldValueIfNecessary("${name}_month","${monthIndex}","option")
                out.println "<option value=\"${monthIndex}\"${i == month ? ' selected="selected"' : ''}>$m</option>"
            }
        }
        out.println '</select>'
    }

    def selectDay(out, attrs) {
        def xdefault = attrs['default']
        if (xdefault == null) {
            xdefault = new Date()
        }
        else if (xdefault.toString() != 'none') {
            if (xdefault instanceof String) {
                xdefault = DateFormat.getInstance().parse(xdefault)

            }
            else if (!grailsTagDateHelper.supportsDatePicker(xdefault.class)) {
                throwTagError("Tag [datePicker] the default date is not a supported class")
            }
        }
        else {
            xdefault = null
        }

        def name = attrs.name
        def id = attrs.id ?: name
        def cssClasses = attrs.cssClasses

        if (cssClasses == 'true') {
            cssClasses = DEFAULT_CSS_CLASSES
        }

        def value = attrs.value
        if (value.toString() == 'none') {
            value = null
        }
        else if (!value) {
            value = xdefault
        }

        def c = null
        if (value instanceof Calendar) {
            c = value
        }
        else if (value != null) {
            c = grailsTagDateHelper.buildCalendar(value)
        }

        def day
        if (c != null) {
            day = c.get(GregorianCalendar.DAY_OF_MONTH)
        }

        def noSelection = attrs.noSelection
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        out.println "<select name=\"${name}_day\" id=\"${id}_day\" aria-labelledby=\"${name} ${name}_day\""
        if (attrs.disabled) {
            out << ' disabled="disabled"'
        }
        if (attrs.readonly) {
            out << ' readonly="readonly"'
        }
        if (cssClasses) {
            out << "class=\"" + cssClasses['day'] + "\""
        }
        out << '>'

        if (noSelection) {
            renderNoSelectionOptionImpl(out, noSelection.key, noSelection.value, '')
            out.println()
        }

        for (i in 1..31) {
            // Change this option to use requestDataValueProcessor
            def dayIndex = processFormFieldValueIfNecessary("${name}_day","${i}","option")
            out.println "<option value=\"${dayIndex}\"${i == day ? ' selected="selected"' : ''}>${i}</option>"
        }
        out.println '</select>'
    }

    def selectHour(out, attrs) {
        def xdefault = attrs['default']
        if (xdefault == null) {
            xdefault = new Date()
        }
        else if (xdefault.toString() != 'none') {
            if (xdefault instanceof String) {
                xdefault = DateFormat.getInstance().parse(xdefault)

            }
            else if (!grailsTagDateHelper.supportsDatePicker(xdefault.class)) {
                throwTagError("Tag [datePicker] the default date is not a supported class")
            }
        }
        else {
            xdefault = null
        }

        def name = attrs.name
        def id = attrs.id ?: name
        def cssClasses = attrs.cssClasses

        if (cssClasses == 'true') {
            cssClasses = DEFAULT_CSS_CLASSES
        }

        def value = attrs.value
        if (value.toString() == 'none') {
            value = null
        }
        else if (!value) {
            value = xdefault
        }

        def precision = (attrs.precision ? PRECISION_RANKINGS[attrs.precision] :
            (grailsApplication.config.grails.tags.datePicker.default.precision ?
                PRECISION_RANKINGS["${grailsApplication.config.grails.tags.datePicker.default.precision}"] :
                PRECISION_RANKINGS["minute"]))

        def c = null
        if (value instanceof Calendar) {
            c = value
        }
        else if (value != null) {
            c = grailsTagDateHelper.buildCalendar(value)
        }

        def hour
        if (c != null) {
            hour = c.get(GregorianCalendar.HOUR_OF_DAY)
        }

        def noSelection = attrs.noSelection
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        out.println "<select name=\"${name}_hour\" id=\"${id}_hour\" aria-labelledby=\"${name} ${name}_hour\""
        if (attrs.disabled) {
            out << ' disabled="disabled"'
        }
        if (attrs.readonly) {
            out << ' readonly="readonly"'
        }
        if (cssClasses) {
            out << "class=\"" + cssClasses['hour'] + "\""
        }
        out << '>'

        if (noSelection) {
            renderNoSelectionOptionImpl(out, noSelection.key, noSelection.value, '')
            out.println()
        }

        for (i in 0..23) {
            def h = '' + i
            if (i < 10) h = '0' + h
            // This option add hour to requestDataValueProcessor
            h  = processFormFieldValueIfNecessary("${name}_hour","${h}","option")
            out.println "<option value=\"${h}\"${i == hour ? ' selected="selected"' : ''}>$h</option>"
        }
        out.println '</select> :'

        // If we're rendering the hour, but not the minutes, then display the minutes as 00 in read-only format
        if (precision < PRECISION_RANKINGS["minute"]) {
            out.println '00'
        }
    }

    def selectMinute(out, attrs) {
        def xdefault = attrs['default']
        if (xdefault == null) {
            xdefault = new Date()
        }
        else if (xdefault.toString() != 'none') {
            if (xdefault instanceof String) {
                xdefault = DateFormat.getInstance().parse(xdefault)

            }
            else if (!grailsTagDateHelper.supportsDatePicker(xdefault.class)) {
                throwTagError("Tag [datePicker] the default date is not a supported class")
            }
        }
        else {
            xdefault = null
        }

        def name = attrs.name
        def id = attrs.id ?: name
        def cssClasses = attrs.cssClasses

        if (cssClasses == 'true') {
            cssClasses = DEFAULT_CSS_CLASSES
        }

        def value = attrs.value
        if (value.toString() == 'none') {
            value = null
        }
        else if (!value) {
            value = xdefault
        }

        def c = null
        if (value instanceof Calendar) {
            c = value
        }
        else if (value != null) {
            c = grailsTagDateHelper.buildCalendar(value)
        }

        def minute
        if (c != null) {
            minute = c.get(GregorianCalendar.MINUTE)
        }

        def noSelection = attrs.noSelection
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        out.println "<select name=\"${name}_minute\" id=\"${id}_minute\" aria-labelledby=\"${name} ${name}_minute\""
        if (attrs.disabled) {
            out << 'disabled="disabled"'
        }
        if (attrs.readonly) {
            out << 'readonly="readonly"'
        }
        if (cssClasses) {
            out << "class=\"" + cssClasses['minute'] + "\""
        }
        out << '>'

        if (noSelection) {
            renderNoSelectionOptionImpl(out, noSelection.key, noSelection.value, '')
            out.println()
        }

        for (i in 0..59) {
            def m = '' + i
            if (i < 10) m = '0' + m
            m  = processFormFieldValueIfNecessary("${name}_minute","${m}","option")
            out.println "<option value=\"${m}\"${i == minute ? ' selected="selected"' : ''}>$m</option>"
        }
        out.println '</select>'
    }

    def selectSecond(out, attrs) {
        def xdefault = attrs['default']
        if (xdefault == null) {
            xdefault = new Date()
        }
        else if (xdefault.toString() != 'none') {
            if (xdefault instanceof String) {
                xdefault = DateFormat.getInstance().parse(xdefault)

            }
            else if (!grailsTagDateHelper.supportsDatePicker(xdefault.class)) {
                throwTagError("Tag [datePicker] the default date is not a supported class")
            }
        }
        else {
            xdefault = null
        }

        def name = attrs.name
        def id = attrs.id ?: name
        def cssClasses = attrs.cssClasses

        if (cssClasses == 'true') {
            cssClasses = DEFAULT_CSS_CLASSES
        }

        def value = attrs.value
        if (value.toString() == 'none') {
            value = null
        }
        else if (!value) {
            value = xdefault
        }

        def c = null
        if (value instanceof Calendar) {
            c = value
        }
        else if (value != null) {
            c = grailsTagDateHelper.buildCalendar(value)
        }

        def second
        if (c != null) {
            second = c.get(GregorianCalendar.SECOND)
        }

        def noSelection = attrs.noSelection
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }

        out.println "<select name=\"${name}_second\" id=\"${id}_second\" aria-labelledby=\"${name} ${name}_second\""
        if (attrs.disabled) {
            out << 'disabled="disabled"'
        }
        if (attrs.readonly) {
            out << 'readonly="readonly"'
        }
        if (cssClasses) {
            out << "class=\"" + cssClasses['second'] + "\""
        }
        out << '>'

        if (noSelection) {
            renderNoSelectionOptionImpl(out, noSelection.key, noSelection.value, '')
            out.println()
        }

        for (i in 0..59) {
            def m = '' + i
            if (i < 10) m = '0' + m
            m  = processFormFieldValueIfNecessary("${name}_second","${m}","option")
            out.println "<option value=\"${m}\"${i == second ? ' selected="selected"' : ''}>$m</option>"
        }
        out.println '</select>'
    }

    Closure renderNoSelectionOption = {noSelectionKey, noSelectionValue, value ->
        renderNoSelectionOptionImpl(out, noSelectionKey, noSelectionValue, value)
    }

    def renderNoSelectionOptionImpl(out, noSelectionKey, noSelectionValue, value) {
        // If a label for the '--Please choose--' first item is supplied, write it out
        out << "<option value=\"${(noSelectionKey == null ? '' : noSelectionKey)}\"${noSelectionKey == value ? ' selected="selected"' : ''}>${noSelectionValue.encodeAsHTML()}</option>"
    }

    private processFormFieldValueIfNecessary(name, value, type) {
        if (requestDataValueProcessor != null) {
            return requestDataValueProcessor.processFormFieldValue(request, name, "${value}", type)
        }
        return value
    }

    /**
     * Creates next/previous links to support pagination for the current controller.<br/>
     *
     * &lt;g:paginate total="${Account.count()}" /&gt;<br/>
     *
     * @emptyTag
     *
     * @attr total REQUIRED The total number of results to paginate
     * @attr action the name of the action to use in the link, if not specified the default action will be linked
     * @attr controller the name of the controller to use in the link, if not specified the current controller will be linked
     * @attr id The id to use in the link
     * @attr params A map containing request parameters
     * @attr prev The text to display for the previous link (defaults to "Previous" as defined by default.paginate.prev property in I18n messages.properties)
     * @attr next The text to display for the next link (defaults to "Next" as defined by default.paginate.next property in I18n messages.properties)
     * @attr omitPrev Whether to not show the previous link (if set to true, the previous link will not be shown)
     * @attr omitNext Whether to not show the next link (if set to true, the next link will not be shown)
     * @attr omitFirst Whether to not show the first link (if set to true, the first link will not be shown)
     * @attr omitLast Whether to not show the last link (if set to true, the last link will not be shown)
     * @attr max The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty
     * @attr maxsteps The number of steps displayed for pagination (defaults to 10). Used ONLY if params.maxsteps is empty
     * @attr offset Used only if params.offset is empty
     * @attr mapping The named URL mapping to use to rewrite the link
     * @attr fragment The link fragment (often called anchor tag) to use
     */
    Closure paginate = { Map attrsMap ->
        TypeConvertingMap attrs = (TypeConvertingMap)attrsMap
        def writer = out
        if (attrs.total == null) {
            throwTagError("Tag [paginate] is missing required attribute [total]")
        }

        def messageSource = grailsAttributes.messageSource
        def locale = RCU.getLocale(request)

        def total = attrs.int('total') ?: 0
        def offset = attrs.int('offset') ?: params.int('offset') ?: 0
        def max = params.int('max')
        def maxsteps = (attrs.int('maxsteps') ?: 10)
        if (!max) max = (attrs.int('max') ?: 10)

        Map linkParams = [:]
        if (attrs.params instanceof Map) linkParams.putAll((Map)attrs.params)
        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        Map linkTagAttrs = [:]
        def action
        if (attrs.containsKey('mapping')) {
            linkTagAttrs.mapping = attrs.mapping
            action = attrs.action
        } else {
            action = attrs.action ?: params.action
        }
        if (action) {
            linkTagAttrs.action = action
        }
        if (attrs.controller) {
            linkTagAttrs.controller = attrs.controller
        }
        if (attrs.containsKey(UrlMapping.PLUGIN)) {
            linkTagAttrs.put(UrlMapping.PLUGIN, attrs.get(UrlMapping.PLUGIN))
        }
        if (attrs.containsKey(UrlMapping.NAMESPACE)) {
            linkTagAttrs.put(UrlMapping.NAMESPACE, attrs.get(UrlMapping.NAMESPACE))
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        if (attrs.fragment != null) {
            linkTagAttrs.fragment = attrs.fragment
        }
        linkTagAttrs.params = linkParams

        // determine paging variables
        def steps = maxsteps > 0
        int currentstep = ((offset / max) as int) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil((total / max) as double)) as int

        // display previous link when not on firststep unless omitPrev is true
        if (currentstep > firststep && !attrs.boolean('omitPrev')) {
            linkTagAttrs.put('class', 'page-link')
            linkParams.offset = offset - max
            writer << '<li class="page-item">'
            writer << callLink((Map)linkTagAttrs.clone()) {
                (attrs.prev ?: messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            }
            writer << '</li>'
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.put('class', 'page-link')

            // determine begin and endstep paging variables
            int beginstep = currentstep - (Math.round(maxsteps / 2.0d) as int) + (maxsteps % 2)
            int endstep = currentstep + (Math.round(maxsteps / 2.0d) as int) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep && !attrs.boolean('omitFirst')) {
                linkParams.offset = 0
                writer << callLink((Map)linkTagAttrs.clone()) {firststep.toString()}
            }
            //show a gap if beginstep isn't immediately after firststep, and if were not omitting first or rev
            if (beginstep > firststep+1 && (!attrs.boolean('omitFirst') || !attrs.boolean('omitPrev')) ) {
                writer << '<span class="step gap">..</span>'
            }

            // display paginate steps
            (beginstep..endstep).each { int i ->
                if (currentstep == i) {
                    writer << '<li class="page-item active">'
                    writer << "<a class=\"page-link\">${i}</a>"
                }
                else {
                    writer << '<li class="page-item">'
                    linkParams.offset = (i - 1) * max
                    writer << callLink((Map)linkTagAttrs.clone()) {i.toString()}
                }
                writer << '</li>'
            }

            //show a gap if beginstep isn't immediately before firststep, and if were not omitting first or rev
            if (endstep+1 < laststep && (!attrs.boolean('omitLast') || !attrs.boolean('omitNext'))) {
                writer << '<span class="step gap">..</span>'
            }
            // display laststep link when endstep is not laststep
            if (endstep < laststep && !attrs.boolean('omitLast')) {
                linkParams.offset = (laststep - 1) * max
                writer << callLink((Map)linkTagAttrs.clone()) { laststep.toString() }
            }
        }

        // display next link when not on laststep unless omitNext is true
        if (currentstep < laststep && !attrs.boolean('omitNext')) {
            linkTagAttrs.put('class', 'page-link')
            writer << '<li class="page-item">'
            linkParams.offset = offset + max
            writer << callLink((Map)linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
            }
            writer << '</li>'
        }
    }

    /**
     * Some attributes can be defined as Boolean values, but the html specification
     * mandates the attribute must have the same value as its name. For example,
     * disabled, readonly and checked.
     */
    @CompileStatic
    private void booleanToAttribute(Map attrs, String attrName) {
        def attrValue = attrs.remove(attrName)
        if (attrValue instanceof CharSequence) {
            attrValue = attrValue.toString().trim()
        }
        // If the value is the same as the name or if it is a boolean value,
        // reintroduce the attribute to the map according to the w3c rules, so it is output later
        if ((attrValue instanceof Boolean && attrValue) ||
            (attrValue instanceof String && (((String)attrValue).equalsIgnoreCase("true") || ((String)attrValue).equalsIgnoreCase(attrName)))) {
            attrs.put(attrName, attrName)
        } else if (attrValue instanceof String && !((String)attrValue).equalsIgnoreCase("false")) {
            // If the value is not the string 'false', then we should just pass it on to
            // keep compatibility with existing code
            attrs.put(attrName, attrValue)
        }
    }

    private callLink(Map attrs, Object body) {
        TagOutput.captureTagOutput(tagLibraryLookup, 'g', 'link', attrs, body, OutputContextLookupHelper.lookupOutputContext())
    }

    @Override
    void setConfiguration(Config co) {
        // Some attributes can be treated as boolean, but must be converted to the
        // expected value.
        booleanAttributes = co.getProperty('grails.tags.booleanToAttributes', List, ['disabled', 'checked', 'readonly'])
    }
}

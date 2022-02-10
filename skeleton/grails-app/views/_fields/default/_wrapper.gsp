<div class="form-group row ${invalid ? 'has-error' : ''}">
    <label for="${property}" class="col-3 col-form-label property-label text-right">${label} <g:if test="${required}"><span class="required-indicator">*</span></g:if></label>

    <div class="col-9">
        <f:widget bean="${bean}" property="${property}"/>
        <g:if test="${errors}">
            <g:each in="${errors}" var="error">
                <span class="help-block"><g:message error="${error}"/></span>
            </g:each>
        </g:if>
    </div>
</div>
<g:if test="${constraints?.inList}">
<g:select name="${property}" from="${constraints.inList}" value="${value}" class="col-2 form-control"/>
</g:if>
<g:elseif test="${constraints?.widget == 'textarea'}">
<g:textArea name="${property}" value="${value}" rows="3" class="form-control" id="widget.${property}"/>
</g:elseif>
<g:else>
<g:textField name="${property}" value="${value}" required="${required}" class="col-8 form-control" id="widget.${property}"/>
</g:else>

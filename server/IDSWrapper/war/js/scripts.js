function doSearch() {
	$("#search_results").empty();
	$.getJSON('./eldis-ids/search/' + $("#searchTerm").val(), function(data) {
		$.each(data.results, function(index, item) {
			var link = $("<a>").attr("href",item.url).text(item.label);
			var entry = $("<li>");
			entry.append(link);
			$("#search_results").append(entry);
		});
	});
	return false;
}
function doSearch() {
	$("#search_results").empty();
	$.getJSON('./eldis-ids/search/' + $("#searchTerm").val(), function(data) {
		if (data.results.length > 0) {
			var message = $("<div>").attr("class","alert alert-success").text(data.results.length + " results !");
			$("#search_results").append(message);
			
			var list = $("<ul>");
			console.log(data.results.length);
			$.each(data.results, function(index, item) {
				var link = $("<a>").attr("href",
						window.location + "/" + item.url).text(item.label);
				var entry = $("<li>");
				entry.append(link);
				list.append(entry);
			});
			$("#search_results").append(list);
		} else {
			var message = $("<div>").attr("class","alert alert-error").text("No results :-(");
			$("#search_results").append(message);
			
		}
	});
	return false;
}

function update(event) {
	if (event.keyCode == 13) {
		doSearch();
	}
}

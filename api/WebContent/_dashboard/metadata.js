$(document).ready(function() {
    fetchMetadata();

    function fetchMetadata() {
        $.ajax({
            url: '../api/metadata',
            type: 'GET',
            success: function(response) {
                displayMetadata(response);
            },
            error: function(error) {
                console.error('Error fetching metadata:', error);
                $('#metadataDisplay').html('<p style="color: red;">Failed to load metadata. Please try again later.</p>');
            }
        });
    }

    function displayMetadata(data) {
        let html = '<h2>Metadata</h2>';

        data.forEach(function(table) {
            // Create a header for each table name
            html += `<div class="table-header">${table.tableName}</div>`;
            html += '<table class="table table-bordered" style="margin-bottom: 20px;">';
            html += '<thead><tr><th>Attribute</th><th>Type</th></tr></thead><tbody>';

            // Loop through each column and add rows
            table.columns.forEach(function(column) {
                html += `<tr><td>${column.columnName}</td><td>${column.dataType}</td></tr>`;
            });

            html += '</tbody></table>';
        });

        $('#metadataDisplay').html(html);
    }
});





import React, { useEffect, useState } from 'react';
import ReactPaginate from 'react-paginate';
import { fetchActivityLogs } from '../../api/api';
import '../../static/css/ActivityLog.css';

const ActivityLog = () => {
    const [logs, setLogs] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const itemsPerPage = 10;

    useEffect(() => {
        const loadLogs = async () => {
            try {
                const fetchedLogs = await fetchActivityLogs();
                setLogs(fetchedLogs);
            } catch (error) {
                console.error('Error loading activity logs:', error);
            }
        };
        loadLogs();
    }, []);

    const handlePageClick = (data) => {
        setCurrentPage(data.selected);
    };

    const offset = currentPage * itemsPerPage;
    const currentLogs = logs.slice(offset, offset + itemsPerPage);
    const pageCount = Math.ceil(logs.length / itemsPerPage);

    return (
        <div className="activity-log-container">
            <h2>User Activity Log</h2>

            {logs.length === 0 ? (
                <p>No activity logs available</p>
            ) : (
                <>
                    <table className="activity-log-table">
                        <thead>
                            <tr>
                                <th>User</th>
                                <th>Video</th>
                                <th>Action</th>
                                <th>Timestamp</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentLogs.map(log => (
                                <tr key={log.id}>
                                    <td>{log.user.username}</td>
                                    <td>{log.video.title}</td>
                                    <td>{log.action}</td>
                                    <td>{new Date(log.timestamp).toLocaleString()}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <ReactPaginate
                        previousLabel={"← Previous"}
                        nextLabel={"Next →"}
                        breakLabel={"..."}
                        pageCount={pageCount}
                        marginPagesDisplayed={2}
                        pageRangeDisplayed={3}
                        onPageChange={handlePageClick}
                        containerClassName={"pagination"}
                        activeClassName={"active"}
                    />
                </>
            )}
        </div>
    );
};

export default ActivityLog;